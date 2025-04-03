package com.example.shippingservice.service;

import com.example.shippingservice.jpa.ShippingEntity;

public interface ShippingService {
    Iterable<ShippingEntity> getDeliveriesByUserId(String userId);
}
