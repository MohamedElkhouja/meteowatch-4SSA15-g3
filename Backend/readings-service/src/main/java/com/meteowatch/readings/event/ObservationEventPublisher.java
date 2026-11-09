package com.meteowatch.readings.event;

import com.meteowatch.readings.model.Releve;
import com.meteowatch.readings.model.TypeMesure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ObservationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ObservationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public ObservationEventPublisher(RabbitTemplate rabbitTemplate,
                                     @Value("${app.messaging.exchange}") String exchange,
                                     @Value("${app.messaging.routing-key-observation-recorded}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    /**
     * Publie un événement ObservationRecorded par mesure (température, humidité, pression, vent, précipitations).
     * Le relevé est déjà sauvegardé : une panne RabbitMQ n'annule pas l'enregistrement (elle est journalisée).
     */
    public void publierObservations(Releve releve) {
        for (TypeMesure type : TypeMesure.values()) {
            Double valeur = type.valeurDe(releve);
            if (valeur == null) {
                continue;
            }
            ObservationRecordedEvent event = new ObservationRecordedEvent(
                    UUID.randomUUID().toString(),
                    Instant.now(),
                    releve.id(),
                    releve.stationId(),
                    releve.region(),
                    type,
                    valeur,
                    type.unite(),
                    releve.horodatage());
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, event);
            } catch (AmqpException e) {
                log.error("Impossible de publier {} ({}) pour le relevé {} : {}",
                        routingKey, type, releve.id(), e.getMessage());
                return;
            }
        }
        log.debug("Événements {} publiés pour le relevé {}", routingKey, releve.id());
    }
}
