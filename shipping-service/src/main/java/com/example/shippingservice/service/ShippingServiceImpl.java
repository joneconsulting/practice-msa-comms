package com.example.shippingservice.service;

import com.example.shippingservice.jpa.ShippingEntity;
import com.example.shippingservice.jpa.ShippingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShippingServiceImpl implements ShippingService {
    ShippingRepository shippingRepository;

    @Autowired
    public ShippingServiceImpl(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    @Override
    public Iterable<ShippingEntity> getDeliveriesByUserId(String userId) {
        return shippingRepository.findByUserId(userId);
    }
}
