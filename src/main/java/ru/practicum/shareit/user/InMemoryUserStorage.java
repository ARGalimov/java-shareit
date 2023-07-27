package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public Integer getNextId() {
        return id++;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }
}