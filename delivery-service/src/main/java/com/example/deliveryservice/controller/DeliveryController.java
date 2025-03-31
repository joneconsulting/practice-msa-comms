package com.example.deliveryservice.controller;

import com.example.deliveryservice.jpa.DeliveryEntity;
import com.example.deliveryservice.service.DeliveryService;
import com.example.deliveryservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class DeliveryController {
    Environment env;
    DeliveryService deliveryService;

    @Autowired
    public DeliveryController(Environment env, DeliveryService deliveryService) {
        this.env = env;
        this.deliveryService = deliveryService;
    }

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Delivery Service on LOCAL PORT %s (SERVER PORT %s)",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"));
    }

    @GetMapping("/{userId}/ordered")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve ordered data (delivery items)");
        Iterable<DeliveryEntity> deliveryList = deliveryService.getDeliveriesByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        deliveryList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        log.info("After retrieved ordered data (delivery items)");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
