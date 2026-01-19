package com.kafka.Orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

public class KafkaDLTConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object>
    dltKafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(
                ContainerProperties.AckMode.MANUAL
        );

        factory.setConcurrency(1);
        return factory;
    }
}

