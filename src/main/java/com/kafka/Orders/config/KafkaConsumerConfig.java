package com.kafka.Orders.config;

import com.orders.avro.OrderEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderEvent> consumerFactory
//            DefaultErrorHandler errorHandler
    ){
     ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
     factory.setConsumerFactory(consumerFactory);
//     factory.setCommonErrorHandler(errorHandler);
     factory.getContainerProperties().setAckMode(
             ContainerProperties.AckMode.MANUAL
     );

     return factory;
    }
}
