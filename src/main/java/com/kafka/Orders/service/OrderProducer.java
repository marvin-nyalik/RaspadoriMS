package com.kafka.Orders.service;

import com.orders.avro.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> template){
        this.kafkaTemplate = template;
    }

    public void send(OrderEvent event){
        kafkaTemplate.send("orders", event.getOrderId().toString(), event);
    }
}
