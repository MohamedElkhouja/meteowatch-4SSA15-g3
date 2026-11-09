package com.meteowatch.readings.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Tâche périodique : revérifie les relevés « à valider » quand Station Service redevient disponible. */
@Component
public class RevalidationJob {

    private static final Logger log = LoggerFactory.getLogger(RevalidationJob.class);

    private final ReleveService releveService;

    public RevalidationJob(ReleveService releveService) {
        this.releveService = releveService;
    }

    @Scheduled(fixedDelayString = "${app.revalidation.delai-ms:60000}",
            initialDelayString = "${app.revalidation.delai-ms:60000}")
    public void revalider() {
        int valides = releveService.revaliderEnAttente();
        if (valides > 0) {
            log.info("{} relevé(s) « à valider » revalidé(s)", valides);
        }
    }
}
