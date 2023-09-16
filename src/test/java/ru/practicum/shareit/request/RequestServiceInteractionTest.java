package ru.practicum.shareit.request;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestServiceInteractionTest {

    @Autowired
    private RequestService service;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void createRequest() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        userService.createUser(UserMapper.toUserDto(user));
        User user1 = User.builder().name("sds").email("sasd@mail.ru").id(2).build();
        userService.createUser(UserMapper.toUserDto(user1));
        ItemRequest request = new ItemRequest(1, "sdfd", user, LocalDateTime.now());

        ItemRequestDto requestDto = service.createRequest(1, RequestMapper.toItemRequestDto(request, new ArrayList<>()));
        assertEquals(requestDto.getId(), 1);
        assertEquals(requestDto.getDescription(), request.getDescription());

    }

    @Test
    @Order(2)
    void getRequests() {
        List<ItemRequestDto> requestDto = service.getRequests(1);
        assertEquals(requestDto.get(0).getId(), 1);
        assertEquals(requestDto.get(0).getDescription(), "sdfd");
    }

    @Test
    @Order(3)
    void getRequestId() {
        ItemRequestDto requestDto = service.getRequestId(1, 1);
        assertEquals(requestDto.getId(), 1);
        assertEquals(requestDto.getDescription(), "sdfd");
    }

    @Test
    @Order(4)
    @Transactional
    void getRequestsAnother() {
        List<ItemRequestDto> requestDto = service.getRequestsAnother(2, 0, 1);
        assertEquals(requestDto.get(0).getId(), 1);
        assertEquals(requestDto.get(0).getDescription(), "sdfd");
    }
}