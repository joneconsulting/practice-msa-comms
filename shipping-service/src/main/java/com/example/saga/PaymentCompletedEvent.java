package com.example.saga;

public class PaymentCompletedEvent {
    public String orderId;
    public String productId;
    public int qty;
    public int totalPrice;
    public boolean simulateCancel;

    public PaymentCompletedEvent() {}
    public PaymentCompletedEvent(String orderId, String productId, int qty, int totalPrice, boolean simulateCancel) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.simulateCancel = simulateCancel;
    }
}
