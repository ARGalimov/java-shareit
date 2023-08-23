package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemMapper {
    Item toEntity(ItemDto dto, Item item);

    ItemDto toDto(Item item);

    List<ItemDto> toDto(Iterable<Item> items);

    Item createNewEntity(User user, ItemDto dto);
}
