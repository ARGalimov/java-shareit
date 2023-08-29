package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.exception.UserCommentException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private UserDto userDto;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto;
    private ItemDto itemDto2;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(300, "First", "first@mail.ru");
        userDto1 = new UserDto(101, "user101", "user101@mail.ru");
        userDto2 = new UserDto(102, "user102", "user102@amail.ru");
        itemDto = new ItemDto(100, "Item1", "Description1", true,
                userDto.getId(), null, null, null, null);
        itemDto2 = new ItemDto(102, "Item2", "Description2", true,
                userDto.getId(), null, null, null, null);
    }

    @Test
    void shouldCreateItem() {
        ObjectNotFoundException exp = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(userDto.getId(), itemDto));
        assertFalse(exp.getMessage().isEmpty());
        UserDto newUserDto = userService.createUser(userDto1);
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), itemDto);
        ItemDto returnItemDto = itemService.findItem(newItemDto.getId(), newUserDto.getId());
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void shouldUpdateItem() {
        ObjectNotFoundException exp = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(userDto.getId(), itemDto.getId(), itemDto));
        assertFalse(exp.getMessage().isEmpty());
        UserDto newUserDto = userService.createUser(userDto1);
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), itemDto);
        ItemDto patchItemDto = new ItemDto();
        patchItemDto.setName("NewName");
        patchItemDto.setDescription("NewDescription");
        ItemDto returnItemDto = itemService.updateItem(newUserDto.getId(), newItemDto.getId(), patchItemDto);
        assertThat(returnItemDto.getName(), equalTo("NewName"));
        assertThat(returnItemDto.getDescription(), equalTo("NewDescription"));
    }

    @Test
    void shouldDeleteItem() {
        UserDto newUserDto = userService.createUser(userDto1);
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), itemDto);
        itemService.deleteItem(newUserDto.getId(), newItemDto.getId());
        List<ItemDto> listItems = itemService.findItemsByOwner(newUserDto.getId(), 0, 10);
        assertEquals(0, listItems.size());
        ObjectNotFoundException exp = assertThrows(ObjectNotFoundException.class,
                () -> itemService.deleteItem(newUserDto.getId(), newItemDto.getId()));
        assertFalse(exp.getMessage().isEmpty());
    }

    @Test
    void shouldExceptionWhenUpdateItemNotOwner() {
        UserDto ownerDto = userService.createUser(userDto);
        UserDto newUserDto = userService.createUser(userDto2);
        ItemDto newItemDto = itemService.createItem(ownerDto.getId(), itemDto);
        ItemDto patchItemDto = new ItemDto();
        patchItemDto.setName("NewName");
        PermissionException exp = assertThrows(PermissionException.class,
                () -> itemService.updateItem(newUserDto.getId(), newItemDto.getId(), patchItemDto));
        assertFalse(exp.getMessage().isEmpty());
    }

    @Test
    void shouldReturnItemsByOwner() {
        UserDto ownerDto = userService.createUser(userDto1);
        itemService.createItem(ownerDto.getId(), itemDto);
        itemService.createItem(ownerDto.getId(), itemDto2);
        List<ItemDto> listItems = itemService.findItemsByOwner(ownerDto.getId(), 0, 10);
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldReturnItemsBySearch() {
        UserDto ownerDto = userService.createUser(userDto1);
        itemService.createItem(ownerDto.getId(), itemDto);
        itemService.createItem(ownerDto.getId(), itemDto2);
        List<ItemDto> listItems = itemService.findItemsByText("item", 0, 1);
        assertEquals(1, listItems.size());
    }

    @Test
    void shouldExceptionWhenCreateCommentWhenUserNotBooker() {
        UserDto ownerDto = userService.createUser(userDto1);
        UserDto newUserDto = userService.createUser(userDto2);
        ItemDto newItemDto = itemService.createItem(ownerDto.getId(), itemDto);
        CommentDto commentDto = new CommentDto(1, "Comment1", "", LocalDateTime.now());
        UserCommentException exp = assertThrows(UserCommentException.class,
                () -> itemService.createComment(newUserDto.getId(), newItemDto.getId(), commentDto));
        assertFalse(exp.getMessage().isEmpty());
    }

    @Test
    void shouldCreateComment() {
        UserDto ownerDto = userService.createUser(userDto1);
        UserDto newUserDto = userService.createUser(userDto2);
        ItemDto newItemDto = itemService.createItem(ownerDto.getId(), itemDto);
        PostBookingDto postBookingDto = new PostBookingDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                newItemDto.getId()
        );
        BookingDto bookingDto = bookingService.create(postBookingDto, newUserDto.getId());
        bookingService.approveBooking(ownerDto.getId(), bookingDto.getId(), true);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CommentDto commentDto = new CommentDto(1, "Comment1", newUserDto.getName(), LocalDateTime.now());
        itemService.createComment(newUserDto.getId(), newItemDto.getId(), commentDto);
        newItemDto = itemService.findItem(newItemDto.getId(), newUserDto.getId());
        assertEquals(1, newItemDto.getComments().size());
    }
}
