package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.RequestOrder;
import com.example.userservice.vo.ResponseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto savedUser;

    @BeforeEach
    void test_createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("edowon0623@gmail.com");
        userDto.setName("Kenneth Lee");
        userDto.setPwd("12345678");

        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");

        savedUser = userService.createUser(userDto);
    }

    @Test
    void test_getUserByUserId() {
    }
}