package com.kafka.Orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaInfraProducerConfig {

    @Bean(name = "defaultRetryTopicKafkaTemplate")
    public KafkaTemplate<Object, Object> defaultRetryTopicKafkaTemplate(
            ProducerFactory<Object, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
