package com.example.deliveryservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderMessage implements Serializable {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;

    public String getProductId() {
        return productId;
    }

    public Integer getQty() {
        return qty;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }
}
