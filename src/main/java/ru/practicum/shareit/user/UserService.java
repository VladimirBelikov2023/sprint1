package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    List<UserDto> getAllUsers();

    UserDto getUser(int id);

    void deleteUser(int id);

    UserDto patchUser(int id, UserDto user);
}
