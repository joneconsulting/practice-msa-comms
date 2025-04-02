package com.example.orderservice.dto;

import com.example.orderservice.event.entity.OrderEvent;
import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;

    private boolean deleted;

    private static final Gson gson = new Gson();

    public void apply(OrderEvent event) {
        switch (event.getEventType()) {
            case EVENT_CREATED:
                applyOrderCreated(event);
                break;

            case EVENT_UPDATED:
                applyOrderUpdated(event);
                break;

            case EVENT_DELETED:
                applyOrderDeleted(event);
                break;

            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }
    }

    private void applyOrderCreated(OrderEvent event) {
        OrderCreatedData data = gson.fromJson(event.getPayload(), OrderCreatedData.class);
        this.userId = data.getUserId();
        this.orderId = data.getOrderId();
        this.productId = data.getProductId();
        this.qty = data.getQty();
        this.unitPrice = data.getUnitPrice();
        this.totalPrice = data.getTotalPrice();

        this.deleted = false;
    }

    private void applyOrderUpdated(OrderEvent event) {
        OrderUpdatedData data = gson.fromJson(event.getPayload(), OrderUpdatedData.class);
        this.productId = data.getProductId();
        this.qty = data.getQty();
        this.unitPrice = data.getUnitPrice();
        this.totalPrice = data.getTotalPrice();
    }

    private void applyOrderDeleted(OrderEvent event) {
        this.deleted = true;
    }
}
