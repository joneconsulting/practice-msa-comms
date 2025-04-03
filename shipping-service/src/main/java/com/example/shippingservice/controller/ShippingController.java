package com.example.shippingservice.controller;

import com.example.shippingservice.jpa.ShippingEntity;
import com.example.shippingservice.service.ShippingService;
import com.example.shippingservice.vo.ResponseOrder;
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
public class ShippingController {
    Environment env;
    ShippingService shippingService;

    @Autowired
    public ShippingController(Environment env, ShippingService shippingService) {
        this.env = env;
        this.shippingService = shippingService;
    }

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Delivery Service on LOCAL PORT %s (SERVER PORT %s)",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"));
    }

    @GetMapping("/{userId}/ordered")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve ordered data (shipping items)");
        Iterable<ShippingEntity> shippingList = shippingService.getDeliveriesByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        shippingList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        log.info("After retrieved ordered data (shipping items)");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
