package com.example.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldSaveUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        User saved = userService.createUser(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
    }

    @Test
    void searchByName_ShouldReturnMatchingUsers() {
        User user1 = new User();
        user1.setName("Bob");
        user1.setEmail("bob@example.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("Bobby");
        user2.setEmail("bobby@example.com");
        userService.createUser(user2);

        List<User> found = userService.searchByName("Bob");
        assertThat(found).hasSize(2);
    }
}