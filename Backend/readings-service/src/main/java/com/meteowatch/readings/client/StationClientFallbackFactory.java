package com.meteowatch.readings.client;

import com.meteowatch.readings.exception.StationIndisponibleException;
import com.meteowatch.readings.exception.StationIntrouvableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback Resilience4j :
 *  - 404 de Station Service  => StationIntrouvableException (le relevé est refusé, 404)
 *  - toute autre erreur (timeout, service arrêté, circuit ouvert) => StationIndisponibleException
 *    (le relevé sera enregistré « à valider » puis revérifié).
 */
@Component
public class StationClientFallbackFactory implements FallbackFactory<StationClient> {

    @Override
    public StationClient create(Throwable cause) {
        return id -> {
            if (cause instanceof FeignException fe && fe.status() == 404) {
                throw new StationIntrouvableException(id);
            }
            throw new StationIndisponibleException("Station Service indisponible", cause);
        };
    }
}
