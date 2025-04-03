package com.example.saga;

public class ShippingFailedEvent {
    public String orderId;
    public String reason;

    public ShippingFailedEvent() {}
    public ShippingFailedEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
}
