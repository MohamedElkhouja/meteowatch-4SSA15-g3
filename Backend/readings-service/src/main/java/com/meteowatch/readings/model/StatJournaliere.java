package com.meteowatch.readings.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/** Statistiques journalières d'une station pour un type de mesure (collection "stats_journalieres"). */
@Document(collection = "stats_journalieres")
@CompoundIndex(name = "station_date_type_idx", def = "{'stationId': 1, 'date': 1, 'type': 1}", unique = true)
public record StatJournaliere(
        @Id String id,
        String stationId,
        LocalDate date,
        TypeMesure type,
        Double min,
        Double max,
        Double moyenne,
        Integer nombreReleves,
        Instant calculeLe) {
}
