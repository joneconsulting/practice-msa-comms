package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.event.service.EventCommandService;
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
public class OrderCommandController {
    Environment env;
//    OrderCommandServiceImpl orderCommandService;
    EventCommandService eventCommandService;

    @Autowired
    public OrderCommandController(Environment env, EventCommandService eventCommandService) {
        this.env = env;
        this.eventCommandService = eventCommandService;
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        /* event publish */
        eventCommandService.createOrder(orderDto);

        log.info("After added orders data");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<ResponseOrder> updateOrder(@PathVariable("userId") String userId,
                                                     @PathVariable("orderId") String orderId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before update orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        orderDto.setOrderId(orderId);
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        /* event publish */
        eventCommandService.updateUser(orderId, orderDto);

        log.info("After updated orders data");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
