package com.example.orderservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;

    public Order createOrder(Order order) {
        // Проверяем, что пользователь существует
        userClient.getUserById(order.getUserId());
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(Long id, Order updated) {
        Order existing = getOrderById(id);
        // Если меняется userId — проверяем, что такой пользователь есть
        if (!existing.getUserId().equals(updated.getUserId())) {
            userClient.getUserById(updated.getUserId());
        }
        existing.setUserId(updated.getUserId());
        existing.setProduct(updated.getProduct());
        existing.setPrice(updated.getPrice());
        return orderRepository.save(existing);
    }

    public Order patchOrder(Long id, Order partial) {
        Order existing = getOrderById(id);
        if (partial.getUserId() != null && !partial.getUserId().equals(existing.getUserId())) {
            userClient.getUserById(partial.getUserId());
            existing.setUserId(partial.getUserId());
        }
        if (partial.getProduct() != null) {
            existing.setProduct(partial.getProduct());
        }
        if (partial.getPrice() != null) {
            existing.setPrice(partial.getPrice());
        }
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        // Проверим, что пользователь существует
        userClient.getUserById(userId);
        return orderRepository.findByUserId(userId);
    }

    public UserDto getUserForOrder(Long orderId) {
        Order order = getOrderById(orderId);
        return userClient.getUserById(order.getUserId());
    }
}