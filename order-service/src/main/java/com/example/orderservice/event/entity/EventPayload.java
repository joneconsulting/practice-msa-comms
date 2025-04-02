package com.example.orderservice.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPayload {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
}
