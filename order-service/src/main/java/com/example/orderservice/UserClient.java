package com.example.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(@Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.userServiceUrl = userServiceUrl;
    }

    public UserDto getUserById(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }
}