package com.example.apigatewayservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bff")
public class BffController {

    private final WebClient.Builder loadBalancedWebClientBuilder;

    public BffController(WebClient.Builder webClientBuilder) {
        this.loadBalancedWebClientBuilder = webClientBuilder;
    }

    // 회원 가입 (User-Service 호출)
    @PostMapping("/signup")
    public Mono<String> signup(@RequestBody Map<String, Object> user) {
        return loadBalancedWebClientBuilder.build()
                .post()
                .uri("http://user-service/users")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 로그인 (User-Service 호출)
    @PostMapping("/login")
    public Mono<String> login(@RequestBody Map<String, Object> credentials) {
        return loadBalancedWebClientBuilder.build()
                .post()
                .uri("http://user-service/login")
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 전체 회원 목록 조회 (User-Service 호출)
    @GetMapping("/users")
    public Mono<List<Object>> getAllUsers() {
        return loadBalancedWebClientBuilder.build()
                .get()
                .uri("http://user-service/users")
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList();
    }

    // 회원 상세 조회 (User-Service 호출)
    @GetMapping("/users/{userId}")
    public Mono<List<Object>> getUser(@PathVariable String userId) {
        return loadBalancedWebClientBuilder.build()
                .get()
                .uri("http://user-service/users/{userId}", userId)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList();
    }

    // 주문하기 (Order-Service 호출)
    @PostMapping("/orders/{userId}")
    public Mono<String> createOrder(@PathVariable String userId, @RequestBody Map<String, Object> orderRequest) {
        return loadBalancedWebClientBuilder.build()
                .post()
                .uri("http://order-service/{userId}/orders", userId)
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 주문 목록 확인 (Order-Service 호출)
    @GetMapping("/orders/{userId}")
    public Mono<List<Object>> getOrders(@PathVariable String userId) {
        return loadBalancedWebClientBuilder.build()
                .get()
                .uri("http://order-service/{userId}/orders", userId)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList();
    }
}