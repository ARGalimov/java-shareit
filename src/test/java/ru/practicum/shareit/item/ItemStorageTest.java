package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemStorageTest {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    public void addRequest() {
        User user = userStorage.save(User.builder()
                .name("Name 1")
                .email("name@name.ru")
                .build());

        itemStorage.save(Item.builder()
                .name("Item 1")
                .description("description1")
                .available(true)
                .owner(user)
                .build());
        itemStorage.save(Item.builder()
                .name("Item 2")
                .description("description2")
                .available(true)
                .owner(user)
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void findAll_whenInvoke_listItemsReturn() {
        List<Item> items = itemStorage.findAll();

        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
    }
}