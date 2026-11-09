package com.meteowatch.station.dto;

import com.meteowatch.station.entity.TypeCapteur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CapteurRequest(
        @NotNull(message = "Le type est obligatoire") TypeCapteur type,
        @NotBlank(message = "L'unite est obligatoire") String unite
) {
}
