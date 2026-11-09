package com.meteowatch.readings.exception;

public class ReleveDejaExistantException extends RuntimeException {

    public ReleveDejaExistantException(String stationId, Object horodatage) {
        super("Un relevé existe déjà pour la station " + stationId + " à " + horodatage);
    }
}
