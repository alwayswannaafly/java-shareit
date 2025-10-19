package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "http://localhost:9090";

    public BookingClient() {
        super();
    }

    public ResponseEntity<Object> createBooking(Long bookerId, Object bookingCreateDto) {
        return post("/bookings", bookerId, bookingCreateDto);
    }

    public ResponseEntity<Object> updateBookingStatus(Long ownerId, Long bookingId, Boolean approved) {
        String path = "/bookings/" + bookingId + "?approved=" + approved;
        return patch(path, ownerId, null);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        return get("/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(Long userId, String state) {
        String path = "/bookings?state=" + state;
        return get(path, userId);
    }

    public ResponseEntity<Object> getBookingsByOwner(Long ownerId, String state) {
        String path = "/bookings/owner?state=" + state;
        return get(path, ownerId);
    }
}