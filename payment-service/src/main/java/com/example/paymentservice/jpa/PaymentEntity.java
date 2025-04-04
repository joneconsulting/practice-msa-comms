package com.example.paymentservice.jpa;

import com.example.paymentservice.config.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Data
@Entity
@Table(name="payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String paymentId;
    private String orderId;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date updatedAt;

    public PaymentEntity() {}
    public PaymentEntity(String paymentId, String orderId, double totalPrice, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
