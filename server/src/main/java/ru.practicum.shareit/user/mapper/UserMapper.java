package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toModel(UserDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }
}
