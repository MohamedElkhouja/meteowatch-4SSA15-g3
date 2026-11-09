package com.meteowatch.readings.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Active la revérification périodique des relevés « à valider ». */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
