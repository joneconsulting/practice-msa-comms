package com.example.saga;

public class PaymentFailedEvent {
    public String orderId;
    public String reason;

    public PaymentFailedEvent() {}
    public PaymentFailedEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
}