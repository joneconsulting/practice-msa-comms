package com.example.orderservice.service;

import com.example.orderservice.config.OrderStatus;
import com.example.orderservice.config.Schemas;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import com.example.saga.OrderCreatedEvent;
import com.example.saga.OrderCreatedV1Event;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderProducer orderProducer;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    @Override
    public OrderCreatedV1Event createOrder(OrderDto orderDto) {
        log.info("Requested an order from {}", orderDto.getUserId());
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);
        orderEntity.setStatus(OrderStatus.PENDING);
        orderRepository.save(orderEntity);

//        OrderDto returnValue = mapper.map(orderEntity, OrderDto.class);
        // Publish an OrderCreatedEvent to notify other services
        OrderCreatedV1Event event = new OrderCreatedV1Event(
                Schemas.V1,
                orderEntity.getOrderId(), orderEntity.getProductId(),
                orderEntity.getQty(), orderEntity.getTotalPrice(),
                orderEntity.isSimulateCancel());

        orderProducer.publishOrder(event);
        log.info("Order Created Successfully. Order ID: {}", orderEntity.getOrderId());

        return event;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = new ModelMapper().map(orderEntity.get(), OrderDto.class);

        return orderDto;
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
