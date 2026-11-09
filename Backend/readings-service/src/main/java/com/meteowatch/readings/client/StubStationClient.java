package com.meteowatch.readings.client;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Faux station-service pour développer sans le service de Maha.
 * Activer avec le profil "stub". Toute station est considérée comme existante et active (ville : Tunis).
 */
@Component
@Profile("stub")
@Primary
public class StubStationClient implements StationClient {

    @Override
    public StationDto getStation(String id) {
        return new StationDto(id, "Station " + id, "Tunis", 36.851, 10.227, true);
    }
}
