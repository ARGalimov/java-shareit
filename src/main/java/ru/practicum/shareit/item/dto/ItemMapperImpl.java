package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    private final ItemStorage itemStorage;

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
    public Item toEntity(ItemDto dto, Integer id) {
        Item item = itemStorage.find(id);
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        if (Objects.nonNull(dto.getName())) {
            name = dto.getName();
        }
        if (Objects.nonNull(dto.getDescription())) {
            description = dto.getDescription();
        }
        if (Objects.nonNull(dto.getAvailable())) {
            available = dto.getAvailable();
        }
        return new Item(
                id,
                name,
                description,
                available,
                item.getOwner(),
                item.getItemRequest()
        );
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