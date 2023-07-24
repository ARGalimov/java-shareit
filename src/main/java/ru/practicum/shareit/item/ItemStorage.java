package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAll();

    Item find(Integer id);

    Item create(Item item);

    Item update(Item item);

    void delete(Integer id);
}
