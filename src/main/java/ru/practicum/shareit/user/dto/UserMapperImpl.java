package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final UserStorage userStorage;

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public User toNewEntity(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getName(),
                dto.getEmail()
        );
    }

    @Override
    public User toEntity(UserDto dto, Integer id) {
        User user = userStorage.find(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        String name = user.getName();
        String email = user.getEmail();
        if (Objects.nonNull(dto.getName())) {
            name = dto.getName();
        }
        if (Objects.nonNull(dto.getEmail())) {
            email = dto.getEmail();
        }
        return new User(
                id,
                name,
                email
        );
    }
}