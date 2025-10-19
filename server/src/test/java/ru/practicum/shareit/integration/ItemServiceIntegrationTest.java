package ru.practicum.shareit.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext
@ActiveProfiles("test")
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createItem_ShouldReturnCorrectItem() {
        // Given
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        UserDto savedUser = userService.createUser(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("A test item");
        itemDto.setAvailable(true);

        // When
        ItemDto result = itemService.createItem(itemDto, savedUser.getId());

        // Then
        assertNotNull(result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("A test item", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void getItemByIdShouldReturnCorrectItem() {
        // Given
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        UserDto savedUser = userService.createUser(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("A test item");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.createItem(itemDto, savedUser.getId());

        // When
        ItemDto result = itemService.getItemById(savedItem.getId(), null);

        // Then
        assertEquals(savedItem.getId(), result.getId());
        assertEquals(savedItem.getName(), result.getName());
    }

}
