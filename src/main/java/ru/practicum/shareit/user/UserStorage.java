package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStorage extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
}