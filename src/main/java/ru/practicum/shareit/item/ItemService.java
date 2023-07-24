package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItem(Integer id);

    List<ItemDto> findItemsByOwner(Integer userId);

    List<ItemDto> findItemsByText(String text);

    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    void deleteItem(Integer userId, Integer id);
}
