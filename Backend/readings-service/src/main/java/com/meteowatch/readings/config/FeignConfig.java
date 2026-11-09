package com.meteowatch.readings.config;

import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.meteowatch.readings.client")
public class FeignConfig {

    /** 1 tentative + 1 retry (100 ms puis 500 ms max) sur erreur réseau. */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 500, 2);
    }
}
