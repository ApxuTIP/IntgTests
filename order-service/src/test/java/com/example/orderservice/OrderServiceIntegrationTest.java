package com.example.orderservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderServiceIntegrationTest {
    
    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    @LocalServerPort
    private int port;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("user.service.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    void createOrder_ShouldCallUserServiceAndSaveOrder() {
        Long userId = 1L;
        stubFor(get(urlEqualTo("/api/users/" + userId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}")
                        .withStatus(200)));

        Order order = new Order();
        order.setUserId(userId);
        order.setProduct("Laptop");
        order.setPrice(1500.0);

        Order createdOrder = orderService.createOrder(order);

        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getProduct()).isEqualTo("Laptop");

        verify(getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }

    @Test
    void createOrder_WhenUserNotFound_ShouldThrowException() {
        Long userId = 99L;
        stubFor(get(urlEqualTo("/api/users/" + userId))
                .willReturn(aResponse().withStatus(404)));

        Order order = new Order();
        order.setUserId(userId);
        order.setProduct("Phone");
        order.setPrice(800.0);

        assertThrows(RuntimeException.class, () -> orderService.createOrder(order));

        assertThat(orderRepository.findAll()).isEmpty();
    }
}