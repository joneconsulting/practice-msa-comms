package com.example.orderservice.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OrderEvent {
    @Id
    @GeneratedValue
    private Long eventId;
    private String orderId;
    private String userId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String payload; // JSON 형식으로 이벤트 데이터 저장

    private LocalDateTime timestamp;
}
