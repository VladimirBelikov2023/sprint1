package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    private RequestService service;
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        UserService userService = new UserServiceImpl(userRepo);
        service = new RequestServiceImpl(userService, requestRepository, itemRepository);
    }

    @DirtiesContext
    @Test
    void createRequest() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequest request = new ItemRequest(1, "sdfd", user, LocalDateTime.now());

        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(request, new ArrayList<>());
        ItemRequestDto requestDto2 = service.createRequest(1, requestDto);
        assertEquals(1, requestDto2.getId());
    }


    @DirtiesContext
    @Test
    void getRequest() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        ItemRequest itemRequest = new ItemRequest(1, "dsd", user, LocalDateTime.now());
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getByRequestId(Mockito.anyInt())).thenReturn(List.of(item));
        ItemRequestDto requestDto = service.getRequestId(1, 1);
        assertEquals("dsd", requestDto.getDescription());
    }

    @DirtiesContext
    @Test
    void createRequestBadOwnerId() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequest request = new ItemRequest(1, "sdfd", user, LocalDateTime.now());

        when(userRepo.findById(Mockito.anyInt())).thenReturn(null);

        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(request, new ArrayList<>());
        Exception e = assertThrows(NoObjectException.class, () -> service.createRequest(1, requestDto));
        assertEquals("User не найден", e.getMessage());
    }

    @DirtiesContext
    @Test
    void createRequestBadDescription() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequest request = new ItemRequest(1, "  ", user, LocalDateTime.now());

        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(request, new ArrayList<>());
        Exception e = assertThrows(ValidationException.class, () -> service.createRequest(1, requestDto));
        assertEquals("Не верное описание", e.getMessage());
    }

    @DirtiesContext
    @Test
    void getRequestsBadOwnerId() {

        when(userRepo.findById(Mockito.anyInt())).thenReturn(null);

        Exception e = assertThrows(NoObjectException.class, () -> service.getRequests(1));
        assertEquals("User не найден", e.getMessage());
    }

    @DirtiesContext
    @Test
    void getRequests() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        ItemRequest itemRequest = new ItemRequest(1, "dsd", user, LocalDateTime.now());
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.getByRequesterId(Mockito.anyInt(), Mockito.any(Sort.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.getByRequestId(Mockito.anyInt())).thenReturn(List.of(item));
        List<ItemRequestDto> ls = service.getRequests(1);
        assertEquals(1, ls.size());
        assertEquals("dsd", ls.get(0).getDescription());
    }

    @DirtiesContext
    @Test
    void getRequestIdBadOwnerId() {
        when(userRepo.findById(Mockito.anyInt())).thenReturn(null);

        Exception e = assertThrows(NoObjectException.class, () -> service.getRequestId(1, 1));
        assertEquals("User не найден", e.getMessage());
    }

    @DirtiesContext
    @Test
    void getRequestIdBadItemId() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(NoObjectException.class, () -> service.getRequestId(1, 1));
        assertEquals("Запрос не найден", e.getMessage());
    }

    @DirtiesContext
    @Test
    void getRequestsAnotheBadOwnerId() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(null);

        Exception e = assertThrows(NoObjectException.class, () -> service.getRequestsAnother(1, 0, 1));
        assertEquals("User не найден", e.getMessage());
    }

    @DirtiesContext
    @Test
    void getRequestsAnotheBadSize() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.getRequestsAnother(1, 0, 0));
        assertEquals("Page size must not be less than one", e.getMessage());
    }


    @DirtiesContext
    @Test
    void getRequestsAnother() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        ItemRequest itemRequest = new ItemRequest(1, "dsd", user, LocalDateTime.now());
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.getAllByAnother(Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.getByRequestId(Mockito.anyInt())).thenReturn(List.of(item));
        List<ItemRequestDto> ls = service.getRequestsAnother(1, 1, 1);
        assertEquals(1, ls.size());
        assertEquals("dsd", ls.get(0).getDescription());
    }

}