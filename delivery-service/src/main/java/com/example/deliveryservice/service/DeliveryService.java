package com.example.deliveryservice.service;

import com.example.deliveryservice.jpa.DeliveryEntity;

public interface DeliveryService {
    Iterable<DeliveryEntity> getDeliveriesByUserId(String userId);
}
