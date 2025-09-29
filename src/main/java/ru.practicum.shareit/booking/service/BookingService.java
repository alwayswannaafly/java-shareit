package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId);

    BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUser(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long ownerId, String state);
}