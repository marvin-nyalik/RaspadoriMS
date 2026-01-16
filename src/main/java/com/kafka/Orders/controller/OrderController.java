package com.kafka.Orders.controller;

import com.kafka.Orders.dto.OrderMapper;
import com.kafka.Orders.dto.OrderRequestDTO;
import com.kafka.Orders.service.OrderProducer;
import com.orders.avro.OrderEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer){
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDTO requestDTO){
        OrderEvent event = OrderMapper.toAvro(requestDTO);
        orderProducer.send(event);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body("Order sent to Kafka");
    }
}
