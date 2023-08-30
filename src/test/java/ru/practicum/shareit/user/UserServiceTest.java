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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = UserDto.builder()
                .id(1)
                .name("User1")
                .email("user1@mail.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(2)
                .name("User2")
                .email("user2@mail.ru")
                .build();
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
        UserDto tmpUserDto = UserDto.builder()
                .id(100)
                .name("tmp")
                .email("tmp@tmp.com")
                .build();
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