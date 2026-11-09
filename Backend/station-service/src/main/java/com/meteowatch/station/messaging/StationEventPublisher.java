package com.meteowatch.station.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public StationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publierStationDesactivee(StationDeactivatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_STATION_DEACTIVATED, event);
            log.info("Evenement StationDeactivated publie pour la station {}", event.stationId());
        } catch (AmqpException e) {
            // Le service reste disponible meme si RabbitMQ est indisponible.
            log.warn("RabbitMQ indisponible, evenement non publie : {}", e.getMessage());
        }
    }
}
