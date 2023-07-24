package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public interface ItemMapper {
    Item toEntity(ItemDto dto, Integer id);

    ItemDto toDto(Item item);

    Item createNewEntity(Integer userId, ItemDto dto);
}