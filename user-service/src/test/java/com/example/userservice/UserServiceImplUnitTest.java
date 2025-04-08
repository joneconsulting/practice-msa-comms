package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Environment env;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Test
    void testCreateUser() {
        // given
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test");
        userDto.setPwd("plain");

        // 암호화는 테스트의 핵심이 아니기 때문에, 빠르고 예측 가능한 결과를 만들기 위해 Mock 처리합니다.
        when(passwordEncoder.encode(any())).thenReturn("encryptedPwd");
        // DB와의 상호작용 없이도 save 호출 결과를 시뮬레이션하기 위해 사용합니다.
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserDto result = userService.createUser(userDto);

        // then
        log.info(result.toString());
        assertNotNull(result.getUserId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByUserId_withMockOrderService() {
        // given
        String userId = "user-123";

        // ① DB 사용자 조회 결과 Mock
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setEmail("mockuser@example.com");
        userEntity.setName("Mock User");
        userEntity.setEncryptedPwd("mockEncryptedPwd");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));

        // ② 주문 서비스의 반환값을 가짜로 구성 (fake response)
        List<ResponseOrder> mockOrders = new ArrayList<>();
        ResponseOrder order1 = new ResponseOrder();
        order1.setProductId("product-001");
        order1.setQty(2);
        order1.setUnitPrice(1000);
        mockOrders.add(order1);

        // RestTemplate.exchange(...) 호출 시 반환될 fake ResponseEntity 설정
        ResponseEntity<List<ResponseOrder>> mockResponse = ResponseEntity.ok(mockOrders);

        when(restTemplate.exchange(
                contains(userId),  // 호출된 URL이 userId를 포함하면 OK
                eq(HttpMethod.GET),
                isNull(),  // HttpEntity
                any(ParameterizedTypeReference.class)  // 응답 타입
        )).thenReturn(mockResponse);

        // when
        UserDto userDto = userService.getUserByUserId(userId);

        // then
        assertNotNull(userDto);
        assertEquals(userId, userDto.getUserId());
        assertEquals(1, userDto.getOrders().size());
        assertEquals("product-001", userDto.getOrders().get(0).getProductId());

        // verify: restTemplate.exchange가 1회 호출되었는지 확인
        verify(restTemplate, times(1)).exchange(
                contains(userId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }
}