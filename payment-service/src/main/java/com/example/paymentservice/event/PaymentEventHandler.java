package com.example.paymentservice.event;

import com.example.paymentservice.config.PaymentStatus;
import com.example.paymentservice.config.Schemas;
import com.example.paymentservice.config.Topics;
import com.example.paymentservice.jpa.PaymentEntity;
import com.example.paymentservice.jpa.PaymentRepository;
import com.example.paymentservice.utils.JsonSchemaValidator;
import com.example.saga.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PaymentEventHandler {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order.created", groupId = "payment-service-group")
    public void processOrderCreated(String message) {
        // Verify JSON Schema
        boolean isValid = JsonSchemaValidator.validate(message, "/schemas/v1/order-created.schema.json");

        if (!isValid) {
            System.err.println("❌ 메시지 유효성 실패 - 무시");
            return;
        }

        boolean paymentSuccessful = true;
        String failReason = "";
        try {
            OrderCreatedV1Event event = objectMapper.readValue(message, OrderCreatedV1Event.class);

            log.info("Received OrderCreatedV1Event for order {}", event.orderId);

            // Create a new payment record in PENDING status
            String paymentId = UUID.randomUUID().toString();
            PaymentEntity payment = new PaymentEntity(paymentId, event.orderId, event.totalPrice, PaymentStatus.PENDING);

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
                PaymentCompletedV1Event completedEvent = new PaymentCompletedV1Event(
                        Schemas.V1,
                        event.orderId, event.productId, event.qty, event.totalPrice, event.simulateCancel);

                String jsonInString = "";
                try {
                    jsonInString = objectMapper.writeValueAsString(completedEvent);
                } catch(JsonProcessingException ex) {
                    log.error("❌ Kafka 메시지 전송 실패[결제 성공]: {}", ex.getMessage());
                }

                kafkaTemplate.send(Topics.PAYMENT_COMPLETED, jsonInString);
                log.info("Payment completed for order {}. Event sent to '{}'.", event.orderId, Topics.PAYMENT_COMPLETED);
            } else {
                // Publish PaymentFailedEvent to Kafka
                PaymentFailedV1Event failedEvent = new PaymentFailedV1Event(Schemas.V1, event.orderId, failReason);

                String jsonInString = "";
                try {
                    jsonInString = objectMapper.writeValueAsString(failedEvent);
                } catch(JsonProcessingException ex) {
                    log.error("❌ Kafka 메시지 전송 실패[결제 실패]: {}", ex.getMessage());
                }

                kafkaTemplate.send(Topics.PAYMENT_FAILED, jsonInString);
                log.error("Payment failed for order {}: {}. Event sent to '{}'.", event.orderId, failReason, Topics.PAYMENT_FAILED);
            }
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패 [order.created]: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "shipping.failed", groupId = "payment-service-group")
    public void compensatePayment(String message) {
        try {
            OrderCreatedV1Event event = objectMapper.readValue(message, OrderCreatedV1Event.class);

            log.info("Received ShippingFailedEvent for order {}. Rolling back payment.", event.orderId);
            PaymentEntity payment = paymentRepository.findByOrderId(event.orderId);
            if (payment != null && payment.getStatus().equals(PaymentStatus.COMPLETED)) {
                // If payment was completed, cancel (refund) it due to shipping failure
                payment.setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
                log.error("Payment for order {} is cancelled (refunded) due to shipping failure.", event.orderId);
            }
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패 [shipping.failed]: {}", e.getMessage());
        }
    }
}
