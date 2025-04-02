package com.example.orderservice.event.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.event.entity.OrderEvent;
import com.example.orderservice.event.repository.OrderEventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventQueryService {
    private final OrderEventRepository orderEventRepository;

    public EventQueryService(OrderEventRepository orderEventRepository) {
        this.orderEventRepository = orderEventRepository;
    }

    public OrderDto replayOrder(String orderId) {
        List<OrderEvent> events = orderEventRepository.findByOrderIdOrderByTimestamp(orderId);

        OrderDto order = new OrderDto();
        events.forEach(order::apply);  // apply 메소드로 상태 복원
//        for (OrderEvent event : events) {
//            order.apply(event);
//        }
        order.setOrderId(orderId);

        return order;
    }

    public List<OrderDto> replayAllOrder(String userId) {
        List<String> orderIds = orderEventRepository.findDistinctByUserIdOrderByTimestamp(userId);

        List<OrderDto> orderList = new ArrayList<>();
        // 이벤트 순차적 replay
        for (String orderId : orderIds) {
            List<OrderEvent> orderEvents = orderEventRepository.findByOrderIdOrderByTimestamp(orderId);

            OrderDto order = new OrderDto();
            orderEvents.forEach(order::apply);  // apply 메소드로 상태 복원
            order.setOrderId(orderId);

            orderList.add(order);
        }

        return orderList;
    }
}
