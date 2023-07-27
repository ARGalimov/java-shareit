package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AuthOwnerException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto findItem(Integer id) {
        Item item = itemStorage.find(id).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", id);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findItemsByOwner(Integer userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemStorage.getAll());
        for (Item item : items) {
            if (Objects.equals(item.getOwner(), userId)) {
                itemsDto.add(itemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemStorage.getAll());
        for (Item item : items) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() &&
                    !text.equals("")) {
                itemsDto.add(itemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        userStorage.find(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Вещь создана");
        return itemMapper.toDto(itemStorage.create(itemMapper.createNewEntity(userId, itemDto)));
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemStorage.find(itemId).orElseThrow(() -> {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        if (Objects.equals(item.getOwner(), userId)) {
            return itemMapper.toDto(itemStorage.update(itemMapper.toEntity(itemDto, item)));
        } else {
            log.warn("Пользователь не владелец");
            throw new AuthOwnerException("Пользователь не владелец");
        }
    }

    @Override
    public void deleteItem(Integer userId, Integer id) {
        itemStorage.delete(id);
    }
}
