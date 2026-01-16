package com.kafka.Orders.dto;

import com.orders.avro.OrderEvent;

public class OrderMapper {

    public static OrderEvent toAvro(OrderRequestDTO orderRequestDTO){
        return OrderEvent.newBuilder().
                setOrderId(orderRequestDTO.getOrderId())
                .setQuantity(orderRequestDTO.getQuantity())
                .setPrice(orderRequestDTO.getPrice())
                .setProduct(orderRequestDTO.getProduct())
                .build();
    }
}
