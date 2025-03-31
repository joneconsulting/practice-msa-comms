package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.OrderDto;
import com.example.deliveryservice.jpa.DeliveryEntity;
import com.example.deliveryservice.jpa.DeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeliveryConsumer {

    private final DeliveryRepository deliveryRepository;

    public DeliveryConsumer(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @KafkaListener(topics = "orders", groupId = "delivery-group")
    public void consumeOrder(OrderDto message) {
        System.out.println("Consumed Order: " + message);

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(message.getOrderId());
        delivery.setUserId(message.getUserId());
        delivery.setProductId(message.getProductId());
        delivery.setQuantity(message.getQty());
        delivery.setTotalPrice(message.getTotalPrice());
        delivery.setDeliveryStatus("배송준비중");

        deliveryRepository.save(delivery);
    }
}
