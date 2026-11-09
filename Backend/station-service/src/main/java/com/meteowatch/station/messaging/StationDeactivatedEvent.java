package com.meteowatch.station.messaging;

import java.time.Instant;

/** Evenement publie quand une station est supprimee (desactivee). */
public record StationDeactivatedEvent(Long stationId, String nom, String ville, Instant dateDesactivation) {
}
