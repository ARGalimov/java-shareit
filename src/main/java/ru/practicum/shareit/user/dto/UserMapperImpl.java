package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
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
    public User toEntity(UserDto dto, User user) {
        return Optional.ofNullable(user)
                .map(existingUser -> new User(existingUser.getId(),
                        Objects.nonNull(dto.getName()) ? dto.getName() : existingUser.getName(),
                        Objects.nonNull(dto.getEmail()) ? dto.getEmail() : existingUser.getEmail()))
                .orElseThrow(() -> {
                    log.warn("Пользователь c id не найден");
                    throw new ObjectNotFoundException("Пользователь не найден");
                });
    }
}