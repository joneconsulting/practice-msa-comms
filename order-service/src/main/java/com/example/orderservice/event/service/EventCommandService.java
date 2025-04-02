package com.example.orderservice.event.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.event.entity.EventPayload;
import com.example.orderservice.event.entity.OrderEvent;
import com.example.orderservice.event.entity.EventType;
import com.example.orderservice.event.repository.OrderEventRepository;
import com.google.gson.Gson;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventCommandService {
    private final OrderEventRepository orderEventRepository;
    private final ApplicationEventPublisher publisher;
    private final Gson gson = new Gson();

    public EventCommandService(OrderEventRepository orderEventRepository, ApplicationEventPublisher publisher) {
        this.orderEventRepository = orderEventRepository;
        this.publisher = publisher;
    }

    public void createOrder(OrderDto orderDto) {
        OrderEvent event = new OrderEvent();
        event.setEventType(EventType.EVENT_CREATED);

        event.setOrderId(orderDto.getOrderId());
        event.setUserId(orderDto.getUserId());
        event.setPayload(gson.toJson(EventPayload.builder()
                .productId(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unitPrice(orderDto.getUnitPrice())
                .totalPrice(orderDto.getTotalPrice())
                .build()));
        event.setTimestamp(LocalDateTime.now());

        orderEventRepository.save(event);
        publisher.publishEvent(event);
    }

    public void updateUser(String orderId, OrderDto orderDto) {
        OrderEvent event = new OrderEvent();
        event.setEventType(EventType.EVENT_UPDATED);
        event.setOrderId(orderId);
        event.setUserId(orderDto.getUserId());
        event.setPayload(gson.toJson(EventPayload.builder()
                .productId(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unitPrice(orderDto.getUnitPrice())
                .totalPrice(orderDto.getTotalPrice())
                .build()));
        event.setTimestamp(LocalDateTime.now());

        orderEventRepository.save(event);
        publisher.publishEvent(event);
    }
}
