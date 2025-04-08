package com.example.userservice.vo;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ResponseOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private String createdAt;

    private String orderId;

//    public ResponseOrder(String orderId, String productId, Integer qty,
//                         Integer unitPrice, Integer totalPrice, String createdAt) {
//        this.orderId = orderId;
//        this.productId = productId;
//        this.qty = qty;
//        this.unitPrice = unitPrice;
//        this.totalPrice = totalPrice;
//        this.createdAt = createdAt;
//    }
}
