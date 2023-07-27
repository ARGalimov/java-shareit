package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    @Override
    public Item createNewEntity(Integer userId, ItemDto dto) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                userId,
                null
        );
    }

    @Override
    public Item toEntity(ItemDto dto, Item item) {
        return Optional.ofNullable(item)
                .map(existingItem -> new Item(existingItem.getId(),
                        Objects.nonNull(dto.getName()) ? dto.getName() : existingItem.getName(),
                        Objects.nonNull(dto.getDescription()) ? dto.getDescription() : existingItem.getDescription(),
                        Objects.nonNull(dto.getAvailable()) ? dto.getAvailable() : existingItem.getAvailable(),
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
                item.getAvailable()
        );
    }
}