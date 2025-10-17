package ru.practicum.shareit.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testEqualsAndHashCode() {
        // Given
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(LocalDateTime.now());
        booking1.setEndDate(LocalDateTime.now().plusHours(1));
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(1L);
        booking2.setStartDate(LocalDateTime.now().plusDays(1));
        booking2.setEndDate(LocalDateTime.now().plusDays(2));
        booking2.setStatus(BookingStatus.APPROVED);

        Booking booking3 = new Booking();
        booking3.setId(2L); // другой ID

        // When & Then
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertNotEquals(new Object(), booking1);
        assertEquals(booking1, booking1);

        // hashCode
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertEquals(31, booking1.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.of(2025, 10, 17, 10, 0));
        booking.setEndDate(LocalDateTime.of(2025, 10, 18, 10, 0));
        booking.setStatus(BookingStatus.WAITING);

        // When
        String result = booking.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("WAITING"));
        assertTrue(result.contains("id=1"));
    }

    @Test
    void testNoArgsConstructorAndAllArgsConstructor() {
        // No-args
        Booking booking1 = new Booking();
        assertNull(booking1.getId());

        // All-args
        Item item = new Item();
        User booker = new User();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking booking2 = new Booking(2L, start, end, BookingStatus.APPROVED, item, booker);
        assertEquals(2L, booking2.getId());
        assertEquals(BookingStatus.APPROVED, booking2.getStatus());
        assertEquals(item, booking2.getItem());
        assertEquals(booker, booking2.getBooker());
    }
}