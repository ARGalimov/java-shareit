package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto create(Integer userId, PostItemRequestDto postItemRequestDto);

    List<ItemRequestDto> findAllByUserID(Integer userId);

    List<ItemRequestDto> findAll(Integer userId, Integer from, Integer size);

    ItemRequestDto findById(Integer userId, Integer requestId);
}
