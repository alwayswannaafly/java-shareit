package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- createBooking ---

    @Test
    void createBooking_ShouldReturnBooking() throws Exception {
        BookingCreateDto requestDto = new BookingCreateDto();
        requestDto.setItemId(1L);
        requestDto.setStartDate(LocalDateTime.now().plusDays(1));
        requestDto.setEndDate(LocalDateTime.now().plusDays(2));

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(WAITING);

        when(bookingService.createBooking(any(BookingCreateDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void createBooking_WithInvalidData_ShouldReturn400() throws Exception {
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setItemId(1L);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // --- updateBookingStatus ---

    @Test
    void updateBookingStatus_ShouldReturnUpdatedBooking() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(APPROVED);

        when(bookingService.updateBookingStatus(eq(1L), eq(true), eq(2L))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    // --- getBookingById ---

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(WAITING);

        when(bookingService.getBookingById(eq(1L), eq(3L))).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    // --- getBookingsByUser ---

    @Test
    void getBookingsByUser_WithDefaultState_ShouldReturnBookings() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(APPROVED);

        when(bookingService.getBookingsByUser(eq(1L), eq("ALL"))).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getBookingsByUser_WithCustomState_ShouldReturnBookings() throws Exception {
        when(bookingService.getBookingsByUser(eq(1L), eq("CURRENT"))).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());
    }

    // --- getBookingsByOwner ---

    @Test
    void getBookingsByOwner_ShouldReturnBookings() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setStatus(WAITING);

        when(bookingService.getBookingsByOwner(eq(5L), eq("ALL"))).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getBookingsByOwner_WithState_ShouldReturnBookings() throws Exception {
        when(bookingService.getBookingsByOwner(eq(5L), eq("PAST"))).thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 5L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());
    }

    // --- Error cases ---

    @Test
    void getBookingById_WhenNotFound_ShouldReturn404() throws Exception {
        when(bookingService.getBookingById(eq(999L), eq(1L)))
                .thenThrow(new IdNotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_WhenValidationFails_ShouldReturn400() throws Exception {
        doThrow(new InvalidInputException("Invalid booking dates"))
                .when(bookingService).createBooking(any(), anyLong());

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStartDate(LocalDateTime.now().plusDays(1));
        dto.setEndDate(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}