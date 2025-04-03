package com.example.shippingservice.event;

import com.example.saga.PaymentCompletedEvent;
import com.example.saga.ShippingCompletedEvent;
import com.example.saga.ShippingFailedEvent;
import com.example.shippingservice.config.ShippingStatus;
import com.example.shippingservice.config.Topics;
import com.example.shippingservice.jpa.ShippingEntity;
import com.example.shippingservice.jpa.ShippingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShippingEventHandler {

    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-completed", groupId = "shipping-service-group")
    public void processPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for order {}");
        // Create a new shipping record with PENDING status
        ShippingEntity shipping = new ShippingEntity(event.orderId, event.productId, event.qty, ShippingStatus.PENDING);
        boolean shippingSuccess = true;
        String failureReason = "";

        // Simulate a shipping failure for large quantities (e.g. quantity > 5)
        if (event.qty > 5) {
            shippingSuccess = false;
            failureReason = "Inventory shortage for quantity " + event.qty;
            shipping.setStatus(ShippingStatus.FAILED);
        } else {
            // Shipping succeeds
            shipping.setStatus(ShippingStatus.SHIPPED);
        }

        // Save shipping record to the database
        shippingRepository.save(shipping);

        if (shippingSuccess && !event.simulateCancel) {
            // Shipping completed normally – publish ShippingCompletedEvent
            ShippingCompletedEvent completedEvent = new ShippingCompletedEvent(event.orderId);
            kafkaTemplate.send(Topics.SHIPPING_COMPLETED, completedEvent);
            log.info("Shipping completed for order {}. Event sent to '{}'.", event.orderId, Topics.SHIPPING_COMPLETED);
        } else {
            // Either shipping failed, or simulateCancel flag forces a compensation flow
            if (shippingSuccess && event.simulateCancel) {
                // The shipment succeeded but we simulate a post-success cancellation
                failureReason = "Simulated cancellation after successful shipping";
                shipping.setStatus(ShippingStatus.CANCELLED);  // Mark shipment as cancelled in DB
                shippingRepository.save(shipping);
                log.error("Order {} shipped successfully, but simulateCancel=true. Initiating compensation...", event.orderId);
            } else {
                // Shipping truly failed
                log.error("Shipping failed for order {}: {}", event.orderId, failureReason);
            }
            // Publish ShippingFailedEvent with the reason for failure or cancellation
            ShippingFailedEvent failedEvent = new ShippingFailedEvent(event.orderId, failureReason);
            kafkaTemplate.send(Topics.SHIPPING_FAILED, failedEvent);
            log.error("ShippingFailedEvent sent for order {} with reason: {}", event.orderId, failureReason);
        }
    }
}