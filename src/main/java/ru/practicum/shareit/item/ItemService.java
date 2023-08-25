package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItem(Integer itemId, Integer userId);

    List<ItemDto> findItemsByOwner(Integer userId);

    List<ItemDto> findItemsByText(String text);

    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    void deleteItem(Integer userId, Integer id);

    void checkPermissions(Integer userId, Item item);

    CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto);
}
