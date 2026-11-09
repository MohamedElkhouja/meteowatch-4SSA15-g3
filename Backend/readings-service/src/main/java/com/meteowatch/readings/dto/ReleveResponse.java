package com.meteowatch.readings.dto;

import com.meteowatch.readings.model.Releve;
import com.meteowatch.readings.model.StatutReleve;

import java.time.Instant;

public record ReleveResponse(
        String id,
        String stationId,
        Instant horodatage,
        Double temperature,
        Double humidite,
        Double pression,
        Double vitesseVent,
        Double precipitation,
        String region,
        StatutReleve statut) {

    public static ReleveResponse from(Releve r) {
        return new ReleveResponse(r.id(), r.stationId(), r.horodatage(), r.temperature(), r.humidite(),
                r.pression(), r.vitesseVent(), r.precipitation(), r.region(), r.statut());
    }
}
