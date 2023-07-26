package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer id = 1;

    @Override
    public Integer getNextId() {
        return id++;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> find(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Integer id) {
        items.remove(id);
    }


}
