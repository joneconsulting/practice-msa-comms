package com.example.orderservice.service;

import com.example.orderservice.config.Topics;
import com.example.orderservice.dto.OrderDto;
import com.example.saga.OrderCreatedEvent;
import com.example.saga.OrderCreatedV1Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrder(OrderCreatedV1Event message) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(message);
        } catch(JsonProcessingException ex) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(Topics.ORDER_CREATED, jsonInString);
//        kafkaTemplate.send(Topics.ORDER_CREATED, message);
    }
}

