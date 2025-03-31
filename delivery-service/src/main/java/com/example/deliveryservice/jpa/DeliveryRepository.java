package com.example.deliveryservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
    Iterable<DeliveryEntity> findByUserId(String userId);
}