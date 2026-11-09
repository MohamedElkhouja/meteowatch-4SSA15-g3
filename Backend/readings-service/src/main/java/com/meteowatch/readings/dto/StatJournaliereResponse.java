package com.meteowatch.readings.dto;

import com.meteowatch.readings.model.StatJournaliere;
import com.meteowatch.readings.model.TypeMesure;

import java.time.LocalDate;

public record StatJournaliereResponse(
        String stationId,
        LocalDate date,
        TypeMesure type,
        Double min,
        Double max,
        Double moyenne,
        Integer nombreReleves) {

    public static StatJournaliereResponse from(StatJournaliere s) {
        return new StatJournaliereResponse(s.stationId(), s.date(), s.type(), s.min(), s.max(), s.moyenne(),
                s.nombreReleves());
    }
}
