package com.kafka.Orders.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

@EnableKafka
@EnableKafkaRetryTopic
@Configuration
public class KafkaRetryConfig {
}
