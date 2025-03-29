package com.example.userservice.vo;

public record OrderDto(String orderId, String productId, int qty, int unitPrice, int totalPrice) {}
