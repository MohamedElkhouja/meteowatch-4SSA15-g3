package com.meteowatch.station.dto;

import com.meteowatch.station.entity.EtatCapteur;
import jakarta.validation.constraints.NotNull;

public record EtatCapteurRequest(@NotNull(message = "L'etat est obligatoire") EtatCapteur etat) {
}
