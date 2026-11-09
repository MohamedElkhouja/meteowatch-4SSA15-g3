package com.meteowatch.readings.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/** Relevé météo d'une station (collection "releves", séries temporelles indexées sur stationId + horodatage). */
@Document(collection = "releves")
@CompoundIndexes({
        @CompoundIndex(name = "station_horodatage_idx", def = "{'stationId': 1, 'horodatage': -1}", unique = true),
        @CompoundIndex(name = "statut_horodatage_idx", def = "{'statut': 1, 'horodatage': 1}")
})
public record Releve(
        @Id String id,
        String stationId,
        Instant horodatage,
        Double temperature,
        Double humidite,
        Double pression,
        Double vitesseVent,
        Double precipitation,
        String region,
        StatutReleve statut,
        Instant creeLe) {
}
