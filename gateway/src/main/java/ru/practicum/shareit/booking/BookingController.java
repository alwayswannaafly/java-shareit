package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long bookerId,
                                                @Valid @RequestBody Object bookingCreateDto) {
        return bookingClient.createBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                                      @PathVariable @Positive Long bookingId,
                                                      @RequestParam Boolean approved) {
        return bookingClient.updateBookingStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable @Positive Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByOwner(ownerId, state);
    }
}