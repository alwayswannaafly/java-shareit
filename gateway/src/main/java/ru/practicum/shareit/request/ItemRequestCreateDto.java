package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 1, max = 1000, message = "Описание должно быть от 1 до 1000 символов")
    private String description;
}