package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto findUser(Integer id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer id);

    void deleteUser(Integer id);
}
