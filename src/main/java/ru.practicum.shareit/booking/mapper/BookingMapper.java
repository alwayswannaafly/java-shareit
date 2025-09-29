package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking, UserDto booker, ItemSimpleDto item) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booker);
        dto.setItem(item);
        return dto;
    }
}
