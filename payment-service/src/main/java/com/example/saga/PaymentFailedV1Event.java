package com.example.saga;

public class PaymentFailedV1Event {
    public String version = "v1";
    public String orderId;
    public String reason;

    public PaymentFailedV1Event() {}
    public PaymentFailedV1Event(String version, String orderId, String reason) {
        this.version = version;
        this.orderId = orderId;
        this.reason = reason;
    }
}