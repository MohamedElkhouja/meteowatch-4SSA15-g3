package com.meteowatch.station.dto;

import com.meteowatch.station.entity.Capteur;
import com.meteowatch.station.entity.EtatCapteur;
import com.meteowatch.station.entity.TypeCapteur;

public record CapteurResponse(Long id, TypeCapteur type, String unite, EtatCapteur etat, Long stationId) {

    public static CapteurResponse from(Capteur c) {
        return new CapteurResponse(c.getId(), c.getType(), c.getUnite(), c.getEtat(), c.getStation().getId());
    }
}
