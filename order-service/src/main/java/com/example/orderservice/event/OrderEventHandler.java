package com.example.orderservice.event;

import com.example.orderservice.config.OrderStatus;
import com.example.orderservice.jpa.OrderRepository;
import com.example.saga.OrderCreatedV1Event;
import com.example.saga.PaymentFailedEvent;
import com.example.saga.ShippingCompletedEvent;
import com.example.saga.ShippingFailedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventHandler {
    @Autowired
    private OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment.failed", groupId = "order-service-group")
    public void handlePaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);

            // Compensation: cancel the order on payment failure
            orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                log.error("Order {} cancelled due to payment failure: {}", order.getId(), event.reason);
            });
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패 [payment.failed]: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "shipping.completed", groupId = "order-service-group")
    public void handleShippingCompleted(String message) {
        try {
            ShippingCompletedEvent event = objectMapper.readValue(message, ShippingCompletedEvent.class);
            // Mark order as completed when shipping succeeds
            orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                log.info("Order {} completed successfully (shipped).", order.getId());
            });
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패 [shipping.completed]: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "shipping.failed", groupId = "order-service-group")
    public void handleShippingFailed(String message) {
        try {
            ShippingFailedEvent event = objectMapper.readValue(message, ShippingFailedEvent.class);
            // Compensation: cancel the order on shipping failure
            orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                log.error("Order {} cancelled due to shipping failure: {}", order.getId() , event.reason);
            });
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패 [shipping.failed]: {}", e.getMessage());
        }
    }
}
