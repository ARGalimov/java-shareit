package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userStorage.getAll();
        for (User user : users) {
            usersDto.add(userMapper.toDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto findUser(Integer id) {
        User user = userStorage.find(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        return userMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.toNewEntity(userDto);
        validateEmail(newUser);
        return userMapper.toDto(userStorage.create(newUser));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userMapper.toEntity(userDto, userStorage.find(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        }));
        if (Objects.nonNull(userDto.getEmail())) {
            validateEmail(user);
        }
        return userMapper.toDto(userStorage.update(user));
    }

    @Override
    public void deleteUser(Integer id) {
        userStorage.delete(id);
    }

    private void validateEmail(User checkedUser) {
        List<User> users = userStorage.getAll();
        for (User user : users) {
            if (!user.getId().equals(checkedUser.getId()) && user.getEmail().equals(checkedUser.getEmail())) {
                throw new UserExistException("Такой пользователь уже существует");
            }
        }
    }
}