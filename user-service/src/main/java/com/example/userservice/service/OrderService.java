package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderService {
    RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retry(name = "orderService", fallbackMethod = "orderListFallback")
    @CircuitBreaker(name = "orderService", fallbackMethod = "orderListFallback")
    @Bulkhead(name = "orderService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "orderListFallback")
    @TimeLimiter(name = "orderService")  // CompletableFuture 반환 메서드에서만 타임아웃 적용
    public CompletableFuture<List<ResponseOrder>> getOrderListByUserId(String userId) {
        log.info("Before call orders microservice: userId [{}]", userId);
        String orderUrl = String.format("http://127.0.0.1:8082/%s/orders", userId);
        ResponseEntity<List<ResponseOrder>> orderListResponse =
                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ResponseOrder>>() {
                        });
        log.info("After called orders microservice using restful api");

        return CompletableFuture.completedFuture(orderListResponse.getBody());
    }

    public CompletableFuture<List<ResponseOrder>> orderListFallback(String userId, Throwable ex) {
        log.error("[Retry+Circuitbreaker] Fallback for order: user_id: {}, {}", userId, ex.getMessage());
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    /* bulkhead demo */
    @Bulkhead(name = "orderService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "bulkheadFallbackOrder")
    public CompletableFuture<String> getOrderAsync(String userId) throws InterruptedException {
        log.info("Processing order for user: {}", userId);
        Thread.sleep(10000); // 일부러 지연

        return CompletableFuture.completedFuture("Order result for " + userId);
    }

    public CompletableFuture<String> bulkheadFallbackOrder(String userId, Throwable t) {
        log.warn("[Bulkhead] Fallback for user: {} due to {}", userId, t.getMessage());
        return CompletableFuture.completedFuture("Fallback result for " + userId);
    }
}
