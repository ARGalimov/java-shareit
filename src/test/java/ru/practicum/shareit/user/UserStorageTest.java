package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserStorageTest {

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    public void addRequest() {
        userStorage.save(User.builder()
                .name("Name 1")
                .email("name@name.ru")
                .build());
        userStorage.save(User.builder()
                .name("Name 2")
                .email("name2@name.ru")
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        userStorage.deleteAll();
    }

    @Test
    void findAll_whenInvoke_listUsersReturn() {
        List<User> users = userStorage.findAll();

        assertEquals(2, users.size());
        assertEquals("Name 1", users.get(0).getName());
    }
}