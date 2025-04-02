package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdatedData {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
}
