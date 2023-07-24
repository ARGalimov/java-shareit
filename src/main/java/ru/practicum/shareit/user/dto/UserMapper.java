package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public interface UserMapper {
    UserDto toDto(User user);

    User toNewEntity(UserDto dto);

    User toEntity(UserDto dto, Integer id);
}