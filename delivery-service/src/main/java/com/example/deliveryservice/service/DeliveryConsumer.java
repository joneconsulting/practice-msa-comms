package com.example.deliveryservice.service;

import com.example.deliveryservice.jpa.DeliveryEntity;
import com.example.deliveryservice.jpa.DeliveryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DeliveryConsumer {

    private final DeliveryRepository deliveryRepository;

    public DeliveryConsumer(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @KafkaListener(topics = "orders", groupId = "delivery-group")
    public void consumeOrder(String message) {
        System.out.println("Consumed Order: " + message);

        Map<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(message, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(map.get("orderId"));
        delivery.setUserId(map.get("userId"));
        delivery.setProductId(map.get("productId"));
        delivery.setQuantity(Integer.parseInt(map.get("qty")));
        delivery.setTotalPrice(Integer.parseInt(map.get("totalPrice")));
        delivery.setDeliveryStatus("배송준비중");

        deliveryRepository.save(delivery);
    }
}
