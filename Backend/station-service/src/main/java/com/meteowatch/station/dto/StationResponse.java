package com.meteowatch.station.dto;

import com.meteowatch.station.entity.Station;

import java.time.LocalDate;
import java.util.List;

public record StationResponse(
        Long id,
        String nom,
        String ville,
        Double latitude,
        Double longitude,
        Double altitude,
        LocalDate dateInstallation,
        boolean active,
        List<CapteurResponse> capteurs
) {
    public static StationResponse from(Station s) {
        return new StationResponse(
                s.getId(), s.getNom(), s.getVille(),
                s.getLatitude(), s.getLongitude(), s.getAltitude(),
                s.getDateInstallation(), s.isActive(),
                s.getCapteurs().stream().map(CapteurResponse::from).toList()
        );
    }
}
