package com.example.orderservice.controller;

import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderGraphQLController {
    @Autowired
    private OrderRepository orderRepo;

    @QueryMapping
    public List<ResponseOrder> ordersByUser(@Argument String userId) {
        // This method corresponds to the GraphQL query 'ordersByUser'
        Iterable<OrderEntity> orderEntities = orderRepo.findByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderEntities.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return result;
    }
}