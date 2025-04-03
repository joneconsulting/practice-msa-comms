package com.example.shippingservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<ShippingEntity, Long> {
    Iterable<ShippingEntity> findByUserId(String userId);
}