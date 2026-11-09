package com.meteowatch.readings.service;

import com.meteowatch.readings.client.StationClient;
import com.meteowatch.readings.client.StationDto;
import com.meteowatch.readings.dto.PageResponse;
import com.meteowatch.readings.dto.ReleveRequest;
import com.meteowatch.readings.dto.ReleveResponse;
import com.meteowatch.readings.dto.StatJournaliereResponse;
import com.meteowatch.readings.event.ObservationEventPublisher;
import com.meteowatch.readings.exception.ReleveDejaExistantException;
import com.meteowatch.readings.exception.ReleveIntrouvableException;
import com.meteowatch.readings.exception.RequeteInvalideException;
import com.meteowatch.readings.exception.StationIndisponibleException;
import com.meteowatch.readings.exception.StationIntrouvableException;
import com.meteowatch.readings.model.Releve;
import com.meteowatch.readings.model.StatJournaliere;
import com.meteowatch.readings.model.StatutReleve;
import com.meteowatch.readings.model.TypeMesure;
import com.meteowatch.readings.repository.ReleveQueryRepository;
import com.meteowatch.readings.repository.ReleveQueryRepository.Agregat;
import com.meteowatch.readings.repository.ReleveRepository;
import com.meteowatch.readings.repository.StatJournaliereRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReleveService {

    private static final Logger log = LoggerFactory.getLogger(ReleveService.class);

    private final ReleveRepository releveRepository;
    private final ReleveQueryRepository queryRepository;
    private final StatJournaliereRepository statRepository;
    private final StationClient stationClient;
    private final ObservationEventPublisher eventPublisher;

    public ReleveService(ReleveRepository releveRepository,
                         ReleveQueryRepository queryRepository,
                         StatJournaliereRepository statRepository,
                         StationClient stationClient,
                         ObservationEventPublisher eventPublisher) {
        this.releveRepository = releveRepository;
        this.queryRepository = queryRepository;
        this.statRepository = statRepository;
        this.stationClient = stationClient;
        this.eventPublisher = eventPublisher;
    }

    // ------------------------------------------------------------------ écriture

    /**
     * Enregistre un relevé.
     *  1. appel synchrone à Station Service (OpenFeign) pour vérifier la station et obtenir sa région ;
     *  2. station inconnue => exception (404) ; Station Service indisponible => relevé « à valider » ;
     *  3. sauvegarde puis publication asynchrone d'ObservationRecorded (seulement si le relevé est valide).
     */
    public ReleveResponse enregistrer(ReleveRequest requete) {
        Instant horodatage = tronquer(requete.horodatage());
        if (releveRepository.existsByStationIdAndHorodatage(requete.stationId(), horodatage)) {
            throw new ReleveDejaExistantException(requete.stationId(), horodatage);
        }

        String region = null;
        StatutReleve statut;
        try {
            StationDto station = stationClient.getStation(requete.stationId());
            if (Boolean.FALSE.equals(station.active())) {
                throw StationIntrouvableException.desactivee(requete.stationId());
            }
            region = station.ville();
            statut = StatutReleve.VALIDE;
        } catch (StationIndisponibleException e) {
            log.warn("Station Service indisponible : relevé de {} enregistré « à valider »", requete.stationId());
            statut = StatutReleve.A_VALIDER;
        }

        Releve sauvegarde = releveRepository.save(new Releve(null, requete.stationId(), horodatage,
                requete.temperature(), requete.humidite(), requete.pression(), requete.vitesseVent(),
                requete.precipitation(), region, statut, Instant.now()));

        if (statut == StatutReleve.VALIDE) {
            eventPublisher.publierObservations(sauvegarde);
        }
        return ReleveResponse.from(sauvegarde);
    }

    /**
     * Revérifie les relevés « à valider » (appelé périodiquement par RevalidationJob).
     * Station trouvée : le relevé devient VALIDE et ObservationRecorded est publié.
     * Station inconnue : les relevés sont supprimés. Station Service toujours en panne : on réessaiera.
     *
     * @return le nombre de relevés validés
     */
    public int revaliderEnAttente() {
        List<Releve> enAttente = releveRepository.findTop100ByStatutOrderByHorodatageAsc(StatutReleve.A_VALIDER);
        Map<String, List<Releve>> parStation = enAttente.stream().collect(Collectors.groupingBy(Releve::stationId));

        int valides = 0;
        for (Map.Entry<String, List<Releve>> entree : parStation.entrySet()) {
            StationDto station;
            try {
                station = stationClient.getStation(entree.getKey());
            } catch (StationIntrouvableException e) {
                log.warn("Station {} inconnue : {} relevé(s) « à valider » supprimé(s)",
                        entree.getKey(), entree.getValue().size());
                releveRepository.deleteAll(entree.getValue());
                continue;
            } catch (StationIndisponibleException e) {
                log.info("Station Service toujours indisponible, nouvel essai plus tard");
                return valides;
            }
            if (Boolean.FALSE.equals(station.active())) {
                log.warn("Station {} désactivée : {} relevé(s) « à valider » supprimé(s)",
                        entree.getKey(), entree.getValue().size());
                releveRepository.deleteAll(entree.getValue());
                continue;
            }
            for (Releve r : entree.getValue()) {
                Releve valide = releveRepository.save(new Releve(r.id(), r.stationId(), r.horodatage(),
                        r.temperature(), r.humidite(), r.pression(), r.vitesseVent(), r.precipitation(),
                        station.ville(), StatutReleve.VALIDE, r.creeLe()));
                eventPublisher.publierObservations(valide);
                valides++;
            }
        }
        return valides;
    }

    public void supprimer(String id) {
        if (!releveRepository.existsById(id)) {
            throw new ReleveIntrouvableException("Relevé " + id + " introuvable");
        }
        releveRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ lecture

    public PageResponse<ReleveResponse> rechercher(String stationId, Instant from, Instant to, int page, int size) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new RequeteInvalideException("'from' doit être antérieur ou égal à 'to'");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "horodatage"));
        Page<Releve> resultat = queryRepository.rechercher(stationId, from, to, pageable);
        return new PageResponse<>(
                resultat.getContent().stream().map(ReleveResponse::from).toList(),
                resultat.getNumber(),
                resultat.getSize(),
                resultat.getTotalElements(),
                resultat.getTotalPages());
    }

    public List<ReleveResponse> derniers(String stationId, int limite) {
        return releveRepository.findByStationIdOrderByHorodatageDesc(stationId, PageRequest.of(0, limite))
                .stream().map(ReleveResponse::from).toList();
    }

    public ReleveResponse trouverParId(String id) {
        return releveRepository.findById(id)
                .map(ReleveResponse::from)
                .orElseThrow(() -> new ReleveIntrouvableException("Relevé " + id + " introuvable"));
    }

    // ------------------------------------------------------------------ statistiques

    /** Min, max, moyenne d'une journée (UTC) pour un type de mesure. Les jours passés sont mis en cache. */
    public StatJournaliereResponse statistiques(String stationId, LocalDate date, TypeMesure type) {
        LocalDate aujourdhui = LocalDate.now(ZoneOffset.UTC);
        if (date.isAfter(aujourdhui)) {
            throw new RequeteInvalideException("La date ne peut pas être dans le futur");
        }

        Optional<StatJournaliere> existante = statRepository.findByStationIdAndDateAndType(stationId, date, type);
        if (date.isBefore(aujourdhui) && existante.isPresent()) {
            return StatJournaliereResponse.from(existante.get());
        }

        Instant debut = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant fin = debut.plus(1, ChronoUnit.DAYS);
        Agregat agregat = queryRepository.agreger(stationId, debut, fin, type.champ())
                .orElseThrow(() -> new ReleveIntrouvableException(
                        "Aucun relevé pour la station " + stationId + " le " + date));

        StatJournaliere stat = new StatJournaliere(
                existante.map(StatJournaliere::id).orElse(null),
                stationId,
                date,
                type,
                arrondir(agregat.min()),
                arrondir(agregat.max()),
                arrondir(agregat.moyenne()),
                agregat.nombreReleves(),
                Instant.now());
        return StatJournaliereResponse.from(statRepository.save(stat));
    }

    // ------------------------------------------------------------------ utilitaires

    /** MongoDB stocke les dates à la milliseconde. */
    private Instant tronquer(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MILLIS);
    }

    private Double arrondir(Double valeur) {
        return valeur == null ? null : Math.round(valeur * 100.0) / 100.0;
    }
}
