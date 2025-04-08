package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.ResponseOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class UserServiceComponentTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean  // 실제 빈 대신 가짜로 주입됨
    private RestTemplate restTemplate;

    @Test
    void testGetUserWithMockedOrderService() {
        // given
        UserEntity user = new UserEntity();
        user.setUserId("comp-123");
        user.setEmail("component@test.com");
        user.setName("Comp Test");
        user.setEncryptedPwd("enc");
        userRepository.save(user);

        List<ResponseOrder> mockOrders = new ArrayList<>();
        mockOrders.add(new ResponseOrder());

        when(restTemplate.exchange(
                contains("comp-123"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(mockOrders));

        // when
        UserDto result = userService.getUserByUserId("comp-123");

        // then
        assertNotNull(result);
        assertEquals("component@test.com", result.getEmail());
        assertEquals(1, result.getOrders().size());
    }
}

