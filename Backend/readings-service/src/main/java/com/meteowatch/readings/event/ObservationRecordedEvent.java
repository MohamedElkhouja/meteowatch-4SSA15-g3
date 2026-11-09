package com.meteowatch.readings.event;

import com.meteowatch.readings.model.TypeMesure;

import java.time.Instant;

/** Événement ObservationRecorded (routing key : observation.recorded), un par mesure du relevé. */
public record ObservationRecordedEvent(
        String eventId,
        Instant occurredAt,
        String observationId,
        String stationId,
        String region,
        TypeMesure type,
        Double valeur,
        String unite,
        Instant horodatage) {
}
