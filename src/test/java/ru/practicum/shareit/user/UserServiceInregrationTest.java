package ru.practicum.shareit.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class UserServiceInregrationTest {

    @Autowired
    private UserService userService;


    @Test
    @Order(1)
    void createUser() {
        User user = new User(1, "dssd", "dsd@mail.ru");

        UserDto user1 = userService.createUser(UserMapper.toUserDto(user));

        assertEquals(1, user1.getId());
    }

    @Test
    @Order(1)
    void createUserWrongEmail() {
        List<UserDto> userDto = userService.getAllUsers();
        UserDto user = userService.getAllUsers().get(0);
        user.setId(3);
        Exception e = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertEquals("Дубликат электронного адреса пользователя", e.getMessage());
    }

    @Test
    @Order(4)
    void getUser() {
        UserDto userDto = userService.getUser(1);
        assertEquals(1, userDto.getId());
        assertEquals("dssd", userDto.getName());
        assertEquals("dsd@mail.ru", userDto.getEmail());
    }

    @Test
    @Order(5)
    void patchUser() {
        UserDto userDto1 = new UserDto(1, "sssss", "wwwww@mail.ru");
        UserDto userDto = userService.patchUser(1, userDto1);
        assertEquals(userDto.getName(), userDto1.getName());
        assertEquals(userDto.getEmail(), userDto1.getEmail());
    }

    @Test
    @Order(6)
    void getListUser() {
        UserDto userDto1 = new UserDto(1, "sssss", "wwwww@mail.ru");
        List<UserDto> userDto = userService.getAllUsers();
        assertEquals(userDto.get(0).getName(), userDto1.getName());
        assertEquals(userDto.get(0).getEmail(), userDto1.getEmail());
    }

}