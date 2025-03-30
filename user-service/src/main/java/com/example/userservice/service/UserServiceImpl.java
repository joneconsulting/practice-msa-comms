package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.OrderDto;
import com.example.userservice.vo.ResponseOrder;
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

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    RestTemplate restTemplate;

    CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(username);

        if (optionalUserEntity == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new User(optionalUserEntity.get().getEmail(), optionalUserEntity.get().getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           RestTemplate restTemplate,
                           CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<UserEntity> userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException("User not found");

        log.info("Before call orders microservice");

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(userEntity.get(), UserDto.class);

        // GraphQL query to fetch orders for the user, requesting specific fields
        String orderServiceGraphQLEndpoint = "http://localhost:8082/graphql";
        String graphQLQuery = """
            query($uid: ID!) {
              ordersByUser(userId: $uid) {
                orderId
                productId
                qty
                unitPrice
                totalPrice
              }
            }
            """;
        // Build the request payload
        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("query", graphQLQuery);
        Map<String, Object> vars = new HashMap<>();
        vars.put("uid", userId);
        jsonRequest.put("variables", vars);

        // Send POST request to Order Service GraphQL endpoint
        ResponseEntity<Map> response = restTemplate.postForEntity(orderServiceGraphQLEndpoint, jsonRequest, Map.class);
        // The response body will contain data under the "data" key
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("data")) {
            return userDto;
        }

        // Extract the orders list from the response
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        List<Map<String, Object>> ordersData = (List<Map<String, Object>>) data.get("ordersByUser");
        // Convert to OrderDTO list
        List<OrderDto> ordersList = new ArrayList<>();
        for (Map<String, Object> orderMap : ordersData) {
            OrderDto dto = new OrderDto(
                    (String)orderMap.get("orderId"),
                    (String)orderMap.get("productId"),
                    (Integer) orderMap.get("qty"),
                    (Integer) orderMap.get("unitPrice"),
                    (Integer) orderMap.get("totalPrice"));
            ordersList.add(dto);
        }

        userDto.setOrders(ordersList);
        log.info("After called orders microservice using graphql api");

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity == null)
            throw new UsernameNotFoundException(email);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(optionalUserEntity.get(), UserDto.class);
        return userDto;
    }
}
