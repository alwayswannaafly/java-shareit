package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@DirtiesContext
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_ShouldReturnCorrectBooking() {
        // Given
        UserDto user = new UserDto();
        user.setName("Booker");
        user.setEmail("booker@example.com");
        UserDto booker = userService.createUser(user);

        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        UserDto savedOwner = userService.createUser(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("A test item");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.createItem(itemDto, savedOwner.getId());

        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStartDate(LocalDateTime.now().plusDays(1));
        bookingDto.setEndDate(LocalDateTime.now().plusDays(2));

        // When
        BookingDto result = bookingService.createBooking(bookingDto, booker.getId());

        // Then
        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(savedItem.getId(), result.getItem().getId());
    }

    @Test
    void getBookingById_ShouldReturnCorrectBooking() {
        // Given
        UserDto user = new UserDto();
        user.setName("Booker");
        user.setEmail("booker@example.com");
        UserDto booker = userService.createUser(user);

        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        UserDto savedOwner = userService.createUser(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("A test item");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.createItem(itemDto, savedOwner.getId());

        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStartDate(LocalDateTime.now().plusDays(1));
        bookingDto.setEndDate(LocalDateTime.now().plusDays(2));
        BookingDto savedBooking = bookingService.createBooking(bookingDto, booker.getId());

        // When
        BookingDto result = bookingService.getBookingById(savedBooking.getId(), booker.getId());

        // Then
        assertEquals(savedBooking.getId(), result.getId());
        assertEquals(savedBooking.getItem().getId(), result.getItem().getId());
    }
}