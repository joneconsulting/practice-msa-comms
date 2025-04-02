package com.example.orderservice.event.consumer;

import com.example.orderservice.event.entity.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventListener {

    @EventListener
    public void handleUserCreatedEvent(OrderEvent event) {
        log.info("{}에 의해 주문 생성됨: {} ", event.getUserId(), event.getOrderId());

        // 추가 처리 로직 (예: 알림 발송, 이메일 전송 등)
        sendWelcomeEmail(event.getOrderId());
    }

    private void sendWelcomeEmail(String email) {
        // 이메일 전송 로직
    }
}