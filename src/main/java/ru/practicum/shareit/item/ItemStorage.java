package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Integer getNextId();

    List<Item> getAll();

    Optional<Item> find(Integer id);

    Item create(Item item);

    Item update(Item item);

    void delete(Integer id);
}
