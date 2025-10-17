package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        BookingDto bookingDto = bookingService.createBooking(bookingCreateDto, bookerId);
        return ResponseEntity.ok(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        BookingDto bookingDto = bookingService.updateBookingStatus(bookingId, approved, ownerId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getBookingsByUser(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getBookingsByOwner(ownerId, state);
        return ResponseEntity.ok(bookings);
    }
}
