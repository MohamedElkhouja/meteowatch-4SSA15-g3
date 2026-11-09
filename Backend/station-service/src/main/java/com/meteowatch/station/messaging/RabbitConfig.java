package com.meteowatch.station.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /** Exchange commun a tous les microservices MeteoWatch. */
    public static final String EXCHANGE = "meteowatch.events";

    /** Cle de routage de l'evenement publie par ce service. */
    public static final String RK_STATION_DEACTIVATED = "station.deactivated";

    @Bean
    public TopicExchange meteowatchExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
