package com.example.shippingservice.jpa;

import com.example.shippingservice.config.ShippingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Entity
@Data
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String userId;
    private String productId;
    private Integer qty;
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date updatedAt;

    public ShippingEntity() {}
    public ShippingEntity(String orderId, String productId, int qty, ShippingStatus status) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.status = status;
    }
}

