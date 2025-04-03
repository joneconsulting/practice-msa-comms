package com.example.orderservice.event;

import com.example.orderservice.config.OrderStatus;
import com.example.orderservice.jpa.OrderRepository;
import com.example.saga.PaymentFailedEvent;
import com.example.saga.ShippingCompletedEvent;
import com.example.saga.ShippingFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventHandler {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "payment-failed", groupId = "order-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // Compensation: cancel the order on payment failure
        orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.error("Order {} cancelled due to payment failure: {}", order.getId() , event.reason);
        });
    }

    @KafkaListener(topics = "shipping-completed", groupId = "order-service-group")
    public void handleShippingCompleted(ShippingCompletedEvent event) {
        // Mark order as completed when shipping succeeds
        orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            log.info("Order {} completed successfully (shipped).", order.getId());
        });
    }

    @KafkaListener(topics = "shipping-failed", groupId = "order-service-group")
    public void handleShippingFailed(ShippingFailedEvent event) {
        // Compensation: cancel the order on shipping failure
        orderRepository.findByOrderId(event.orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.error("Order {} cancelled due to shipping failure: {}", order.getId() , event.reason);
        });
    }
}
