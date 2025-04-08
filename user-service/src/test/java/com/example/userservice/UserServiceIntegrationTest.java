package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockRestServiceServer
public class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setup() {
        // 수동으로 바인딩하는 방식입니다:
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void testGetUserByUserIdWithFakeHttpOrderService() {
        // given
        String userId = "int-456";
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail("int@test.com");
        user.setName("Int Test");
        user.setEncryptedPwd("pwd");
        userRepository.save(user);

        String url = "http://127.0.0.1:8082/" + userId + "/orders";

        server.expect(requestTo(url))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        // when
        UserDto result = userService.getUserByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getOrders().size());
    }
}