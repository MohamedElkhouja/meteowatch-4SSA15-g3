package com.meteowatch.readings.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client OpenFeign vers Station Service (appel synchrone).
 * url vide => résolution via Eureka ; sinon URL directe (voir station-service.url).
 */
@FeignClient(name = "station-service",
        url = "${station-service.url:}",
        fallbackFactory = StationClientFallbackFactory.class,
        primary = false)
public interface StationClient {

    @GetMapping("/api/stations/{id}")
    StationDto getStation(@PathVariable("id") String id);
}
