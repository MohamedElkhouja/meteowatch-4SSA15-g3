package com.meteowatch.station.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record StationRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "La ville est obligatoire") String ville,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        Double altitude,
        @PastOrPresent LocalDate dateInstallation
) {
}
