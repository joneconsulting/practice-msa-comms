package com.example.saga;

public class ShippingCompletedEvent {
    public String orderId;

    public ShippingCompletedEvent() {}
    public ShippingCompletedEvent(String orderId) {
        this.orderId = orderId;
    }
}
