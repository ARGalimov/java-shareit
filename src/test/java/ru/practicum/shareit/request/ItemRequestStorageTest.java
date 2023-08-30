package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestStorageTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;

    @BeforeEach
    public void addRequest() {
        User user = userStorage.save(User.builder()
                .name("Name 1")
                .email("box@mail.ru")
                .build());

        itemRequestStorage.save(ItemRequest.builder()
                .description("Some descr")
                .requester(user)
                .created(LocalDateTime.now())
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        itemRequestStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void findAll_whenInvoke_listUsersReturn() {
        List<ItemRequest> itemRequests = itemRequestStorage.findAll();

        assertEquals(1, itemRequests.size());
        assertEquals("Some descr", itemRequests.get(0).getDescription());
    }
}