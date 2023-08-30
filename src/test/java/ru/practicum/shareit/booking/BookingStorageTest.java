package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingStorageTest {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private BookingStorage bookingStorage;

    @BeforeEach
    public void addRequest() {
        User user1 = userStorage.save(User.builder()
                .name("Name 1")
                .email("name1@mail.ru")
                .build());
        User user2 = userStorage.save(User.builder()
                .name("Name 2")
                .email("name2@mail.ru")
                .build());

        Item item = itemStorage.save(Item.builder()
                .name("Item 1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());

        bookingStorage.save(Booking.builder()
                .start(LocalDateTime.of(2025, 1, 5, 2, 0, 0))
                .end(LocalDateTime.of(2025, 1, 20, 10, 0, 0))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void findAll_whenInvoke_listBookingsReturn() {
        List<Booking> bookings = bookingStorage.findAll();

        assertEquals(1, bookings.size());
    }
}