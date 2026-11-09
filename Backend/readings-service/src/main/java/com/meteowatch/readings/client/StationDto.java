package com.meteowatch.readings.client;

/**
 * Réponse de station-service (GET /api/stations/{id}). Seuls les champs utiles sont déclarés,
 * les autres (altitude, dateInstallation, capteurs...) sont ignorés.
 */
public record StationDto(String id, String nom, String ville, Double latitude, Double longitude, Boolean active) {
}
