package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_ShouldReturnCorrectUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        // When
        UserDto result = userService.createUser(userDto);

        // Then
        assertNotNull(result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldReturnCorrectUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto savedUser = userService.createUser(userDto);

        // When
        UserDto result = userService.getUserById(savedUser.getId());

        // Then
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto savedUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated User");

        // When
        UserDto result = userService.updateUser(savedUser.getId(), updateDto);

        // Then
        assertEquals("Updated User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnUsers() {
        // Given
        UserDto user1 = new UserDto();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userService.createUser(user1);

        UserDto user2 = new UserDto();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userService.createUser(user2);

        // When
        var result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("User 1")));
        assertTrue(result.stream().anyMatch(u -> u.getName().equals("User 2")));
    }
}