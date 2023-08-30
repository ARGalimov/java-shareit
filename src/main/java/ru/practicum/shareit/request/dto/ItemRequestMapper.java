package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        UserDto requesterDto = UserMapper.toDto(itemRequest.getRequester());
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requesterDto,
                itemRequest.getCreated(),
                Collections.emptyList()
        );
    }

    public static List<ItemRequestDto> toNewEntity(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (var el : itemRequests) {
            result.add(toDto(el));
        }

        return result;
    }

    public static ItemRequest toEntity(User requester, PostItemRequestDto postItemRequestDto,
                                       LocalDateTime date) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(postItemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(date);
        return itemRequest;
    }
}
