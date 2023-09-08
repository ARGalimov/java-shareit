package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.OffsetPage;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public ItemRequestDto create(Integer userId, PostItemRequestDto postItemRequestDto) {
        User requester = UserMapper.toNewEntity(userService.findUser(userId));
        ItemRequest itemRequest = ItemRequestMapper.toEntity(requester, postItemRequestDto,
                LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllByUserID(Integer userId) {
        userService.findUser(userId);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterId(userId, Sort.by("created"));
        List<ItemRequestDto> dtoItemRequests = ItemRequestMapper.toNewEntity(itemRequests);
        dtoItemRequests.forEach(this::loadItems);
        return dtoItemRequests;
    }

    @Override
    public List<ItemRequestDto> findAll(Integer userId, Integer from, Integer size) {
        Pageable pageable = new OffsetPage(from, size, Sort.by("created"));
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIdNot(userId, pageable);
        List<ItemRequestDto> dtoItemRequests = ItemRequestMapper.toNewEntity(itemRequests);
        dtoItemRequests.forEach(this::loadItems);
        return dtoItemRequests;
    }

    @Override
    public ItemRequestDto findById(Integer userId, Integer requestId) {
        userService.findUser(userId);
        ItemRequest itemRequests = itemRequestStorage.findById(requestId).orElseThrow(() ->
                throwNotFoundException(requestId));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequests);
        loadItems(itemRequestDto);
        return itemRequestDto;
    }

    private void loadItems(ItemRequestDto itemRequestDto) {
        List<Item> items = itemStorage.findAllByItemRequestId(itemRequestDto.getId());
        itemRequestDto.setItems(ItemMapper.toDto(items));
    }

    private ObjectNotFoundException throwNotFoundException(Integer id) {
        String message = "Запрос с id " + id + " не найден!";
        log.warn(message);
        return new ObjectNotFoundException(message);
    }
}
