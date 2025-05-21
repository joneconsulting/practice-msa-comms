package com.example.userservice.controller;

import com.example.userservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/bulkhead")
    public String testBulkhead() throws InterruptedException {
        for (int i = 0; i < 5; i++) { // 0 (정상실행), 1 (스레드풀), 2,3,4 (Bulkhead fallback method)
            final int idx = i;
            orderService.getOrderAsync("user" + idx)
                    .thenAccept(result -> System.out.println(">> " + result));
        }
        return "Bulkhead test started";
    }
}