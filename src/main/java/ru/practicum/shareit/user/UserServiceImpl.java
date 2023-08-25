package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userStorage.findAll();
        for (User user : users) {
            usersDto.add(UserMapper.toDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto findUser(Integer id) {
        User user = userStorage.findById(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.toNewEntity(userDto);
//        validateEmail(newUser);
        return UserMapper.toDto(userStorage.save(newUser));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userStorage.findById(id).orElseThrow(() -> throwObjectNotFoundException(id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(user);
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userStorage.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Integer id) {
        Optional<User> user = userStorage.findById(id);
        user.ifPresent(userStorage::delete);
    }

    private void validateEmail(User checkedUser) {
        List<User> users = userStorage.findAll();
        for (User user : users) {
            if (!user.getId().equals(checkedUser.getId()) && user.getEmail().equals(checkedUser.getEmail())) {
                throw new UserExistException("Такой пользователь уже существует");
            }
        }
    }

    private ObjectNotFoundException throwObjectNotFoundException(Integer id) {
        String message = "Пользователь с id " + id + " не найден!.";
        log.warn(message);
        return new ObjectNotFoundException(message);
    }
}