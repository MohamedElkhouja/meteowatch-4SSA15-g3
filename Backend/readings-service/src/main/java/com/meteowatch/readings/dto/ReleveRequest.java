package com.meteowatch.readings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record ReleveRequest(
        @NotBlank String stationId,
        @NotNull @PastOrPresent Instant horodatage,
        @NotNull @DecimalMin("-90.0") @DecimalMax("60.0") Double temperature,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double humidite,
        @NotNull @DecimalMin("800.0") @DecimalMax("1100.0") Double pression,
        @NotNull @DecimalMin("0.0") Double vitesseVent,
        @NotNull @DecimalMin("0.0") Double precipitation) {
}
