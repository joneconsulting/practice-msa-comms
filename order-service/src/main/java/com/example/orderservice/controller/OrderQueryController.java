package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.event.service.EventQueryService;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderCommandServiceImpl;
import com.example.orderservice.service.OrderQueryServiceImpl;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@Slf4j
public class OrderQueryController {
    Environment env;
    EventQueryService eventQueryService;

    @Autowired
    public OrderQueryController(Environment env, EventQueryService eventQueryService) {
        this.env = env;
        this.eventQueryService = eventQueryService;
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");
        List<OrderDto> orderList = eventQueryService.replayAllOrder(userId);

        log.info("After retrieved orders data");

        return ResponseEntity.status(HttpStatus.OK).body(orderList);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("orderId") String orderId) {
        log.info("Before retrieve order data");

        OrderDto orderDto = eventQueryService.replayOrder(orderId);

        log.info("After retrieve order data");
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }
}
