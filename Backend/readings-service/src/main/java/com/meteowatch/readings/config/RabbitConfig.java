package com.meteowatch.readings.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /** Exchange durable partagé par tous les services (les consommateurs créent leurs propres files). */
    @Bean
    public TopicExchange meteowatchExchange(@Value("${app.messaging.exchange}") String name) {
        return new TopicExchange(name, true, false);
    }

    /** Messages en JSON (dates en ISO-8601 grâce à l'ObjectMapper de Spring Boot). */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
