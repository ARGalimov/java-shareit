package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    PostItemRequestDto postItemRequestDto;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        postItemRequestDto = new PostItemRequestDto();
        postItemRequestDto.setDescription("req description");
        userDto1 = new UserDto(101, "user1", "user1@mail.ru");
        userDto2 = new UserDto(102, "user2", "user2@mail.ru");
    }

    @Test
    void shouldCreateItemRequest() {
        UserDto newUserDto = userService.createUser(userDto1);
        ItemRequestDto resultDTO = itemRequestService.create(newUserDto.getId(), postItemRequestDto);
        assertThat(resultDTO.getDescription(), equalTo(postItemRequestDto.getDescription()));
    }

    @Test
    void shouldExceptionWhenCreateItemRequestWithWrongUser() {
        ObjectNotFoundException exp = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.create(100500, postItemRequestDto));
        assertFalse(exp.getMessage().isEmpty());
    }


    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        UserDto firstUserDto = userService.createUser(userDto1);
        ObjectNotFoundException exp = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.findById(firstUserDto.getId(), 100500));
        assertFalse(exp.getMessage().isEmpty());
    }

    @Test
    void shouldReturnAllItemRequests() {
        UserDto firstUserDto = userService.createUser(userDto1);
        UserDto newUserDto = userService.createUser(userDto2);
        ItemRequestDto returnOneRequestDto = itemRequestService.create(newUserDto.getId(), postItemRequestDto);
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(newUserDto.getId(), postItemRequestDto);
        List<ItemRequestDto> listItemRequest = itemRequestService.findAll(firstUserDto.getId(), 0, 10);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnOwnItemRequests() {
        UserDto firstUserDto = userService.createUser(userDto1);
        UserDto newUserDto = userService.createUser(userDto2);
        ItemRequestDto returnOneRequestDto = itemRequestService.create(newUserDto.getId(), postItemRequestDto);
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(newUserDto.getId(), postItemRequestDto);
        List<ItemRequestDto> listItemRequest = itemRequestService.findAllByUserID(newUserDto.getId());
        System.out.println(listItemRequest.toString());
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnItemRequestById() {
        UserDto firstUserDto = userService.createUser(userDto1);
        ItemRequestDto newItemRequestDto = itemRequestService.create(firstUserDto.getId(), postItemRequestDto);
        ItemRequestDto returnItemRequestDto = itemRequestService.findById(firstUserDto.getId(),
                newItemRequestDto.getId());
        assertThat(returnItemRequestDto.getDescription(), equalTo(postItemRequestDto.getDescription()));
    }
}