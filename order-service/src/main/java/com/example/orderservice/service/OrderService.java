package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.saga.OrderCreatedEvent;
import com.example.saga.OrderCreatedV1Event;

public interface OrderService {
    OrderCreatedV1Event createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
