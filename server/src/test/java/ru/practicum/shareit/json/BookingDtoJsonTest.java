package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeBookingDto_ShouldReturnCorrectJson() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStartDate(LocalDateTime.of(2025, 10, 15, 10, 0));
        bookingDto.setEndDate(LocalDateTime.of(2025, 10, 16, 10, 0));
        bookingDto.setStatus(APPROVED);

        // When
        String json = objectMapper.writeValueAsString(bookingDto);

        // Then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\"");
        assertThat(json).contains("\"end\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

    @Test
    void deserializeBookingDto_ShouldReturnCorrectObject() throws Exception {
        // Given
        String json = " {\"id\": 1, \"start\": \"2025-10-15T10:00:00\", " +
                "\"end\": \"2025-10-16T10:00:00\", \"status\": \"APPROVED\"} ";

        // When
        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        // Then
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStartDate())
                .isEqualTo(LocalDateTime.of(2025, 10, 15, 10, 0));
        assertThat(bookingDto.getEndDate())
                .isEqualTo(LocalDateTime.of(2025, 10, 16, 10, 0));
        assertThat(bookingDto.getStatus()).isEqualTo(APPROVED);
    }
}