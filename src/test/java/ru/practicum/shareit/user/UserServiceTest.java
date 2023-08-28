package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto(1, "User1", "user1@mail.ru");
        userDto2 = new UserDto(2, "User2", "user2@mail.ru");
    }

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.createUser(userDto1);
        assertThat(returnUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void shouldDoesNotThrowWhenDeleteUserWithWrongId() {
        assertDoesNotThrow(() -> userService.deleteUser(100500));
    }

    @Test
    void shouldDeleteUser() {
        UserDto tmpUserDto = new UserDto(100, "tmp", "tmp@tmp.com");
        UserDto returnUserDto = userService.createUser(tmpUserDto);
        List<UserDto> listUser = userService.getAll();
        int size = listUser.size();
        userService.deleteUser(returnUserDto.getId());
        listUser = userService.getAll();
        assertThat(listUser.size(), equalTo(size - 1));
    }

    @Test
    void shouldUpdateUser() {
        UserDto returnUserDto = userService.createUser(userDto1);
        UserDto patchUserDto = new UserDto();
        patchUserDto.setName("NewName");
        patchUserDto.setEmail("new@email.ru");
        userService.updateUser(patchUserDto, returnUserDto.getId());
        UserDto updateUserDto = userService.findUser(returnUserDto.getId());
        assertThat(updateUserDto.getName(), equalTo("NewName"));
        assertThat(updateUserDto.getEmail(), equalTo("new@email.ru"));
    }

    @Test
    void shouldExceptionWhenUpdateUserWithExistEmail() {
        UserDto returnUserDto1 = userService.createUser(userDto1);
        UserDto returnUserDto2 = userService.createUser(userDto2);
        UserDto patchUserDto = new UserDto();
        patchUserDto.setEmail(userDto1.getEmail());
        UserExistException exp = Assertions.assertThrows(
                UserExistException.class,
                () -> userService.updateUser(patchUserDto, returnUserDto2.getId()));
        assertFalse(exp.getMessage().isEmpty());
    }
}