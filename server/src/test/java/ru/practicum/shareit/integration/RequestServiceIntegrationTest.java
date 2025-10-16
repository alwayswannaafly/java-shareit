package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext
class RequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Test
    void createItemRequest_ShouldReturnCorrectRequest() {
        // Given
        UserDto user = new UserDto();
        user.setName("Requestor");
        user.setEmail("requestor@example.com");
        UserDto savedUser = userService.createUser(user);

        String description = "Need a drill";

        // When
        ItemRequestDto result = itemRequestService.createItemRequest(savedUser.getId(), description);

        // Then
        assertNotNull(result.getId());
        assertEquals(description, result.getDescription());
    }

    @Test
    void getUserRequests_ShouldReturnRequests() {
        // Given
        UserDto user = new UserDto();
        user.setName("Requestor");
        user.setEmail("requestor@example.com");
        UserDto savedUser = userService.createUser(user);

        String description1 = "Need a drill";
        String description2 = "Need a hammer";
        itemRequestService.createItemRequest(savedUser.getId(), description1);
        itemRequestService.createItemRequest(savedUser.getId(), description2);

        // When
        List<ItemRequestDto> result = itemRequestService.getUserRequests(savedUser.getId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals(description1)));
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals(description2)));
    }
}