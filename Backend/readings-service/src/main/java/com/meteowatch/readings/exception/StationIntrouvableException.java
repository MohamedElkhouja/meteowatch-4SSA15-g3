package com.meteowatch.readings.exception;

public class StationIntrouvableException extends RuntimeException {

    public StationIntrouvableException(String stationId) {
        super("Station " + stationId + " introuvable");
    }

    private StationIntrouvableException(String message, boolean desactivee) {
        super(message);
    }

    /** Station supprimée logiquement (active = false) : on n'accepte plus de relevés pour elle. */
    public static StationIntrouvableException desactivee(String stationId) {
        return new StationIntrouvableException("Station " + stationId + " désactivée", true);
    }
}
