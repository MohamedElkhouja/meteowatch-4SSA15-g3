package com.meteowatch.readings.exception;

public class StationIndisponibleException extends RuntimeException {

    public StationIndisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
