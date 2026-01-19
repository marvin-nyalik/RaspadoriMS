package com.kafka.Orders.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

public class OrderDltReprocessor {

    public OrderDltReprocessor(
            @Qualifier("defaultRetryTopicKafkaTemplate")
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
    }

    @KafkaListener(
            topics = "orders.DLT",
            groupId = "orders-dlt-reprocessor",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void onDltMessage(
            ConsumerRecord<Object, Object> record,
            Acknowledgment ack
    ) {
        System.out.print("Processing failed: " + record);
    }
}
