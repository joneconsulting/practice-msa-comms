package com.example.deliveryservice.service;

import com.example.deliveryservice.jpa.DeliveryEntity;
import com.example.deliveryservice.jpa.DeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class DeliveryServiceImpl implements DeliveryService {
    DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public Iterable<DeliveryEntity> getDeliveriesByUserId(String userId) {
        return deliveryRepository.findByUserId(userId);
    }
}
