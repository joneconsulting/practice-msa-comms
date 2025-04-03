package com.example.paymentservice.event;

import com.example.paymentservice.config.PaymentStatus;
import com.example.paymentservice.config.Topics;
import com.example.paymentservice.jpa.PaymentEntity;
import com.example.paymentservice.jpa.PaymentRepository;
import com.example.saga.OrderCreatedEvent;
import com.example.saga.PaymentCompletedEvent;
import com.example.saga.PaymentFailedEvent;
import com.example.saga.ShippingFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PaymentEventHandler {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void processOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order {}", event.orderId);
        // Create a new payment record in PENDING status
        String paymentId = UUID.randomUUID().toString();
        PaymentEntity payment = new PaymentEntity(paymentId, event.orderId, event.totalPrice, PaymentStatus.PENDING);
        boolean paymentSuccessful = true;
        String failReason = "";

        // Simulate a payment failure for large amounts (e.g. > 1000)
        if (event.totalPrice > 100000) {
            paymentSuccessful = false;
            failReason = "Insufficient funds";
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            // Payment succeeds
            payment.setStatus(PaymentStatus.COMPLETED);
        }

        // Save the payment result to the database
        paymentRepository.save(payment);

        if (paymentSuccessful) {
            // Publish PaymentCompletedEvent to Kafka
            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    event.orderId, event.productId, event.qty, event.totalPrice, event.simulateCancel);
            kafkaTemplate.send(Topics.PAYMENT_COMPLETED, completedEvent);
            log.info("Payment completed for order {}. Event sent to '{}'.", event.orderId, Topics.PAYMENT_COMPLETED);
        } else {
            // Publish PaymentFailedEvent to Kafka
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.orderId, failReason);
            kafkaTemplate.send(Topics.PAYMENT_FAILED, failedEvent);
            log.error("Payment failed for order {}: {}. Event sent to '{}'.", event.orderId, failReason, Topics.PAYMENT_FAILED);
        }
    }

    @KafkaListener(topics = "shipping-failed", groupId = "payment-service-group")
    public void compensatePayment(ShippingFailedEvent event) {
        log.info("Received ShippingFailedEvent for order {}. Rolling back payment.", event.orderId);
        PaymentEntity payment = paymentRepository.findByOrderId(event.orderId);
        if (payment != null && payment.getStatus().equals(PaymentStatus.COMPLETED)) {
            // If payment was completed, cancel (refund) it due to shipping failure
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
            log.error("Payment for order {} is cancelled (refunded) due to shipping failure.", event.orderId);
        }
    }
}
