package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Integer getNextId();

    List<User> getAll();

    Optional<User> find(Integer id);

    User create(User user);

    User update(User user);

    void delete(Integer id);
}