package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;
    @Mock
    private UserRepo userRepo;


    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepo);
    }

    @DirtiesContext
    @Test
    void createUser() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();

        when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = userService.createUser(userDto);
        assertEquals(1, userDto1.getId());

        verify(userRepo).save(Mockito.any(User.class));
    }

    @DirtiesContext
    @Test
    void createUserNullName() {
        User user = User.builder().email("sd@mail.ru").id(1).build();

        UserDto userDto = UserMapper.toUserDto(user);
        Exception e = assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        assertEquals("Не верные данные объекта", e.getMessage());

        verify(userRepo, never()).save(Mockito.any(User.class));
    }


    @DirtiesContext
    @Test
    void createUserNullEmail() {
        User user = User.builder().name("sds").id(1).build();

        UserDto userDto = UserMapper.toUserDto(user);
        Exception e = assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        assertEquals("Не верные данные объекта", e.getMessage());

        verify(userRepo, never()).save(Mockito.any(User.class));
    }

    @DirtiesContext
    @Test
    void createUserBadEmail() {
        User user = User.builder().name("sds").email("sdsd").id(1).build();

        UserDto userDto = UserMapper.toUserDto(user);
        Exception e = assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        assertEquals("Передан объект с некоректным email", e.getMessage());

        verify(userRepo, never()).save(Mockito.any(User.class));
    }


    @DirtiesContext
    @Test
    void getUser() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();

        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getUser(1);
        assertEquals(1, userDto.getId());
    }

    @DirtiesContext
    @Test
    void getAllUsers() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();

        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserDto> userDto = userService.getAllUsers();
        assertEquals(1, userDto.get(0).getId());
    }

    @DirtiesContext
    @Test
    void delUser() {
        when(userRepo.findAll()).thenReturn(new ArrayList<>());

        userService.deleteUser(1);
        List<UserDto> userDto = userService.getAllUsers();
        assertEquals(0, userDto.size());
    }

    @DirtiesContext
    @Test
    void getUserWrongId() {
        when(userRepo.findById(Mockito.anyInt())).thenThrow(new ValidationException("User не найден"));

        Exception e = assertThrows(ValidationException.class, () -> userService.getUser(45));
        assertEquals("User не найден", e.getMessage());
    }


    @DirtiesContext
    @Test
    void patchUser() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();


        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = userService.patchUser(1, userDto);
        assertEquals(1, userDto1.getId());
        assertEquals("sds", userDto1.getName());

        verify(userRepo).save(Mockito.any(User.class));
    }

    @DirtiesContext
    @Test
    void patchUserWrongId() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();

        when(userRepo.findById(Mockito.anyInt())).thenReturn(null);

        UserDto userDto = UserMapper.toUserDto(user);

        Exception e = assertThrows(NoObjectException.class, () -> userService.patchUser(1, userDto));
        assertEquals("User не найден", e.getMessage());

    }

}