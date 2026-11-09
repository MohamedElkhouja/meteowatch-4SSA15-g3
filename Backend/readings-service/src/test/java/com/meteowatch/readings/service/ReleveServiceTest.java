package com.meteowatch.readings.service;

import com.meteowatch.readings.client.StationClient;
import com.meteowatch.readings.client.StationDto;
import com.meteowatch.readings.dto.ReleveRequest;
import com.meteowatch.readings.dto.ReleveResponse;
import com.meteowatch.readings.event.ObservationEventPublisher;
import com.meteowatch.readings.exception.ReleveDejaExistantException;
import com.meteowatch.readings.exception.RequeteInvalideException;
import com.meteowatch.readings.exception.StationIndisponibleException;
import com.meteowatch.readings.exception.StationIntrouvableException;
import com.meteowatch.readings.model.Releve;
import com.meteowatch.readings.model.StatutReleve;
import com.meteowatch.readings.repository.ReleveQueryRepository;
import com.meteowatch.readings.repository.ReleveRepository;
import com.meteowatch.readings.repository.StatJournaliereRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleveServiceTest {

    @Mock ReleveRepository releveRepository;
    @Mock ReleveQueryRepository queryRepository;
    @Mock StatJournaliereRepository statRepository;
    @Mock StationClient stationClient;
    @Mock ObservationEventPublisher eventPublisher;

    @InjectMocks ReleveService service;

    private ReleveRequest requete() {
        return new ReleveRequest("st-1", Instant.parse("2026-11-09T10:00:00Z"), 22.4, 61.0, 1014.0, 18.0, 0.2);
    }

    private StationDto station() {
        return new StationDto("1", "Tunis-Carthage", "Tunis", 36.851, 10.227, true);
    }

    @Test
    void enregistrer_stationExistante_sauvegardeValideEtPublie() {
        when(releveRepository.existsByStationIdAndHorodatage(eq("st-1"), any())).thenReturn(false);
        when(stationClient.getStation("st-1")).thenReturn(station());
        when(releveRepository.save(any(Releve.class))).thenAnswer(inv -> inv.getArgument(0));

        ReleveResponse reponse = service.enregistrer(requete());

        assertEquals(StatutReleve.VALIDE, reponse.statut());
        assertEquals("Tunis", reponse.region());
        verify(eventPublisher).publierObservations(any(Releve.class));
    }

    @Test
    void enregistrer_stationInconnue_leveExceptionEtNeSauvegardeRien() {
        when(releveRepository.existsByStationIdAndHorodatage(eq("st-1"), any())).thenReturn(false);
        when(stationClient.getStation("st-1")).thenThrow(new StationIntrouvableException("st-1"));

        assertThrows(StationIntrouvableException.class, () -> service.enregistrer(requete()));

        verify(releveRepository, never()).save(any());
        verify(eventPublisher, never()).publierObservations(any());
    }

    @Test
    void enregistrer_stationDesactivee_leveStationIntrouvable() {
        when(releveRepository.existsByStationIdAndHorodatage(eq("st-1"), any())).thenReturn(false);
        when(stationClient.getStation("st-1"))
                .thenReturn(new StationDto("1", "Tunis-Carthage", "Tunis", 36.851, 10.227, false));

        assertThrows(StationIntrouvableException.class, () -> service.enregistrer(requete()));

        verify(releveRepository, never()).save(any());
        verify(eventPublisher, never()).publierObservations(any());
    }

    @Test
    void enregistrer_stationIndisponible_sauvegardeAValiderSansPublier() {
        when(releveRepository.existsByStationIdAndHorodatage(eq("st-1"), any())).thenReturn(false);
        when(stationClient.getStation("st-1")).thenThrow(new StationIndisponibleException("panne", null));
        when(releveRepository.save(any(Releve.class))).thenAnswer(inv -> inv.getArgument(0));

        ReleveResponse reponse = service.enregistrer(requete());

        assertEquals(StatutReleve.A_VALIDER, reponse.statut());
        verify(eventPublisher, never()).publierObservations(any());
    }

    @Test
    void enregistrer_doublon_leveConflit() {
        when(releveRepository.existsByStationIdAndHorodatage(eq("st-1"), any())).thenReturn(true);

        assertThrows(ReleveDejaExistantException.class, () -> service.enregistrer(requete()));

        verify(releveRepository, never()).save(any());
    }

    @Test
    void revaliderEnAttente_stationDisponible_validePuisPublie() {
        Releve enAttente = new Releve("r-1", "st-1", Instant.parse("2026-11-09T10:00:00Z"),
                22.4, 61.0, 1014.0, 18.0, 0.2, null, StatutReleve.A_VALIDER, Instant.now());
        when(releveRepository.findTop100ByStatutOrderByHorodatageAsc(StatutReleve.A_VALIDER))
                .thenReturn(List.of(enAttente));
        when(stationClient.getStation("st-1")).thenReturn(station());
        when(releveRepository.save(any(Releve.class))).thenAnswer(inv -> inv.getArgument(0));

        int valides = service.revaliderEnAttente();

        assertEquals(1, valides);
        ArgumentCaptor<Releve> captor = ArgumentCaptor.forClass(Releve.class);
        verify(eventPublisher).publierObservations(captor.capture());
        assertEquals(StatutReleve.VALIDE, captor.getValue().statut());
        assertEquals("Tunis", captor.getValue().region());
    }

    @Test
    void rechercher_fromApresTo_leveRequeteInvalide() {
        Instant from = Instant.parse("2026-11-10T00:00:00Z");
        Instant to = Instant.parse("2026-11-01T00:00:00Z");

        assertThrows(RequeteInvalideException.class, () -> service.rechercher("st-1", from, to, 0, 20));
    }
}
