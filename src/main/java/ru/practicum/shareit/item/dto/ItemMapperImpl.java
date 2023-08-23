package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    @Override
    public Item createNewEntity(User user, ItemDto dto) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                user,
                null
        );
    }

    @Override
    public Item toEntity(ItemDto dto, Item item) {
        return Optional.ofNullable(item)
                .map(existingItem -> new Item(existingItem.getId(),
                        Objects.nonNull(dto.getName()) ? dto.getName() : existingItem.getName(),
                        Objects.nonNull(dto.getDescription()) ? dto.getDescription() : existingItem.getDescription(),
                        Objects.nonNull(dto.getAvailable()) ? dto.getAvailable() : existingItem.isAvailable(),
                        existingItem.getOwner(),
                        existingItem.getItemRequest()))
                .orElseThrow(() -> {
                    log.warn("Вещь не найдена");
                    throw new ObjectNotFoundException("Вещь не найдена");
                });
    }

    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                null,
                null,
                null,
                Collections.EMPTY_LIST
        );
    }

    @Override
    public List<ItemDto> toDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toDto(item));
        }

        return result;
    }
}