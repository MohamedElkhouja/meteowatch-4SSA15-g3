package com.meteowatch.station.service;

import com.meteowatch.station.dto.*;
import com.meteowatch.station.entity.Capteur;
import com.meteowatch.station.entity.Station;
import com.meteowatch.station.exception.ResourceNotFoundException;
import com.meteowatch.station.messaging.StationDeactivatedEvent;
import com.meteowatch.station.messaging.StationEventPublisher;
import com.meteowatch.station.repository.CapteurRepository;
import com.meteowatch.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;
    private final CapteurRepository capteurRepository;
    private final StationEventPublisher eventPublisher;

    public StationService(StationRepository stationRepository,
                          CapteurRepository capteurRepository,
                          StationEventPublisher eventPublisher) {
        this.stationRepository = stationRepository;
        this.capteurRepository = capteurRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<StationResponse> lister(String ville) {
        List<Station> stations = (ville == null || ville.isBlank())
                ? stationRepository.findAll()
                : stationRepository.findByVilleIgnoreCase(ville);
        return stations.stream().map(StationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StationResponse trouver(Long id) {
        return StationResponse.from(getStation(id));
    }

    public StationResponse creer(StationRequest req) {
        Station station = new Station();
        appliquer(station, req);
        return StationResponse.from(stationRepository.save(station));
    }

    public StationResponse modifier(Long id, StationRequest req) {
        Station station = getStation(id);
        appliquer(station, req);
        return StationResponse.from(station);
    }

    /** Suppression logique : la station est desactivee et l'evenement StationDeactivated est publie. */
    public void supprimer(Long id) {
        Station station = getStation(id);
        station.setActive(false);
        eventPublisher.publierStationDesactivee(new StationDeactivatedEvent(
                station.getId(), station.getNom(), station.getVille(), Instant.now()));
    }

    public CapteurResponse ajouterCapteur(Long stationId, CapteurRequest req) {
        Station station = getStation(stationId);
        Capteur capteur = new Capteur();
        capteur.setType(req.type());
        capteur.setUnite(req.unite());
        station.ajouterCapteur(capteur);
        return CapteurResponse.from(capteurRepository.save(capteur));
    }

    public CapteurResponse changerEtatCapteur(Long capteurId, EtatCapteurRequest req) {
        Capteur capteur = capteurRepository.findById(capteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Capteur " + capteurId + " introuvable"));
        capteur.setEtat(req.etat());
        return CapteurResponse.from(capteur);
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station " + id + " introuvable"));
    }

    private void appliquer(Station station, StationRequest req) {
        station.setNom(req.nom());
        station.setVille(req.ville());
        station.setLatitude(req.latitude());
        station.setLongitude(req.longitude());
        station.setAltitude(req.altitude());
        station.setDateInstallation(req.dateInstallation());
    }
}
