package com.example.orderservice.event.repository;

import com.example.orderservice.event.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    List<OrderEvent> findByOrderIdOrderByTimestamp(String orderId);

    @Query("SELECT DISTINCT o.orderId FROM OrderEvent o WHERE o.userId = :userId")
    List<String> findDistinctByUserIdOrderByTimestamp(@Param("userId") String userId);
}
