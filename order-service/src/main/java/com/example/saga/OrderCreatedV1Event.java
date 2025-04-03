package com.example.saga;

public class OrderCreatedV1Event {
    public String version = "v1";
    public String orderId;
    public String productId;
    public int qty;
    public int totalPrice;
    public boolean simulateCancel;

    public OrderCreatedV1Event() {}
    public OrderCreatedV1Event(String version, String orderId, String productId, int qty, int totalPrice, boolean simulateCancel) {
        this.version = version;
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.simulateCancel = simulateCancel;
    }
}