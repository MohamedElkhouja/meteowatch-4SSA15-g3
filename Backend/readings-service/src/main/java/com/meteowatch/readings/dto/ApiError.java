package com.meteowatch.readings.dto;

import java.time.Instant;

/** Format d'erreur commun du projet : { timestamp, status, erreur, message }. */
public record ApiError(
        Instant timestamp,
        int status,
        String erreur,
        String message) {
}
