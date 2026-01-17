package com.kafka.Orders.consumer;

import com.orders.avro.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

@Component
public class OrderConsumer {
    @KafkaListener(
            topics = "orders",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "order-consumer-group"
    )
    public void consume(OrderEvent event, Acknowledgment ack){
        try {
            process(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void process(OrderEvent event) {
        System.out.println("Processed order "+ event.getOrderId()+" successfully!");
    }
}
