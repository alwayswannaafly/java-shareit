package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.user.dto.UserSimpleDto;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = OnCreate.class)
    @Size(min = 1, max = 255, message = "Название должно быть от 1 до 255 символов")
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    @Size(min = 1, max = 1000, message = "Описание должно быть от 1 до 1000 символов")
    private String description;

    @NotNull(message = "Available field is required", groups = OnCreate.class)
    private Boolean available;

    private Long requestId;
    private UserSimpleDto owner;
    private BookingSimpleDto lastBooking;
    private BookingSimpleDto nextBooking;
    private List<CommentDto> comments;
}