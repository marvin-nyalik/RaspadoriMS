package com.kafka.Orders.consumer;

import com.orders.avro.OrderEvent;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

@Component
public class OrderConsumer {
    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT",
            retryTopicSuffix = "-RETRY"
    )
    @KafkaListener(
            topics = "orders",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "order-consumer-group"
    )
    public void consume(OrderEvent event, Acknowledgment ack){
        if(event.getPrice() <= 0 || event.getQuantity() <=0){
            throw new IllegalArgumentException(
                    "Invalid price/quantity { Price: " + event.getPrice()
                            + " Quantity: " + event.getQuantity() + "}"
            );
        }
        process(event);
        ack.acknowledge();
    }

    private void process(OrderEvent event) {
        System.out.println("Processed order "+ event.getOrderId()+" successfully!");
    }
}
