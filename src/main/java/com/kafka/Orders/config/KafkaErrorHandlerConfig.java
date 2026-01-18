package com.kafka.Orders.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaErrorHandlerConfig {
    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (record, ex) -> new TopicPartition(
                record.topic() + ".DLT",
                record.partition())
        );

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer);
        handler.addRetryableExceptions(RuntimeException.class);
        handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}
