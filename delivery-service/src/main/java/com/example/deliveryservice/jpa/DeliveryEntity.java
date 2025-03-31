package com.example.deliveryservice.jpa;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private Integer totalPrice;
    private String deliveryStatus;
    private LocalDateTime createdAt;

    @PrePersist
    void createdAt() {
        this.createdAt = LocalDateTime.now();
    }
}

