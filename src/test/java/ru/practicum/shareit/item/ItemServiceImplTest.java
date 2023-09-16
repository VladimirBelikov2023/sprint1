package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepo userRepo;

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepo, commentRepo, bookingRepository, requestRepository);
    }

    @Test
    void createItemBadUserId() {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).owner(user).name("sd").description("sd").available(true).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(NoObjectException.class, () -> itemService.createItem(ItemMapper.toItemDto(item), 0));
        assertEquals("Пользователь не найден", e.getMessage());
    }

    @Test
    void createItemBadName() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).owner(user).description("sd").available(true).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));


        Exception e = assertThrows(ValidationException.class, () -> itemService.createItem(ItemMapper.toItemDto(item), 0));
        assertEquals("Name пустое", e.getMessage());
    }

    @Test
    void createItemBadAvailable() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").owner(user).description("sd").build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));


        Exception e = assertThrows(ValidationException.class, () -> itemService.createItem(ItemMapper.toItemDto(item), 0));
        assertEquals("Available пустой", e.getMessage());
    }

    @Test
    void createItemBadDescription() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").owner(user).available(true).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));


        Exception e = assertThrows(ValidationException.class, () -> itemService.createItem(ItemMapper.toItemDto(item), 0));
        assertEquals("Description пустое", e.getMessage());
    }

    @Test
    void createItem() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        ItemDto item1 = itemService.createItem(ItemMapper.toItemDto(item), 1);

        assertEquals(item.getDescription(), item1.getDescription());
        assertEquals(item.getName(), item1.getName());
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void createItemWithRequest() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        ItemRequest itemRequest = new ItemRequest(1, "DSd", user, LocalDateTime.now().plusSeconds(1));

        Item item = Item.builder().id(1).name("Sd").description("sds").request(itemRequest).owner(user).available(true).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
        when(requestRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(itemRequest));
        ItemDto item1 = itemService.createItem(ItemMapper.toItemDto(item), 1);

        assertEquals("sds", item1.getDescription());
        assertEquals(1, item1.getRequestId());
        assertEquals(item.getName(), item1.getName());
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void patchItem() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));

        ItemDto item1 = itemService.patchItem(ItemMapper.toItemDto(item), 1, 1);

        assertEquals("sds", item1.getDescription());
        assertEquals(item.getName(), item1.getName());
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void patchItemBadId() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Exception e = assertThrows(NoObjectException.class, () -> itemService.patchItem(ItemMapper.toItemDto(item), 1, 1));
        assertEquals("Item не найден в базе", e.getMessage());

    }

    @Test
    void patchItemBadUserId() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user2).available(true).build();

        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(NoObjectException.class, () -> itemService.patchItem(ItemMapper.toItemDto(item), 1, 1));
        assertEquals("Это не ваша вещь", e.getMessage());

    }

    @Test
    void getItemBadId() {
        Exception e = assertThrows(NoObjectException.class, () -> itemService.getItem(1, 1));
        assertEquals("Неверные данные", e.getMessage());
    }

    @Test
    void getItem() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "Sdsd", user, item);
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));
        when(commentRepo.findByItem(item)).thenReturn(List.of(comment));
        ItemDto item1 = itemService.getItem(1, 1);
        assertEquals("sds", item1.getDescription());
        assert item != null;
        assertEquals(item.getName(), item1.getName());
    }

    @Test
    void getItemNextBooking() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1), item, user, Status.WAITING);
        Comment comment = new Comment(1, "Sdsd", user, item);
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));
        when(commentRepo.findByItem(item)).thenReturn(List.of(comment));
        when(bookingRepository.getByItemIdAndStartingIsAfterAndStatus(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Status.class), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));
        ItemDto items = itemService.getItem(1, 1);
        assertEquals("sds", items.getDescription());
        assertEquals(item.getName(), items.getName());
    }

    @Test
    void getItems() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        when(itemRepository.findAll()).thenReturn(List.of(item));
        List<ItemDto> items = itemService.getItems(1, 0, 1);
        assertEquals("sds", items.get(0).getDescription());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void getItemsNextBooking() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1), item, user, Status.WAITING);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(bookingRepository.getByItemIdAndStartingIsAfterAndStatus(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Status.class), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));
        List<ItemDto> items = itemService.getItems(1, 0, 1);
        assertEquals("sds", items.get(0).getDescription());
        assertEquals(item.getName(), items.get(0).getName());
    }

    @Test
    void search() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        when(itemRepository.search(Mockito.any(), Mockito.any(PageRequest.class))).thenReturn(List.of(item));
        List<ItemDto> items = itemService.search("Sd", 0, 1);
        assertEquals("sds", items.get(0).getDescription());
        assertEquals(item.getName(), items.get(0).getName());

    }

    @Test
    void searchEmpty() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        List<ItemDto> items = itemService.search("", 0, 1);
        assertEquals(0, items.size());


    }

    @Test
    void createComment() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "sdsd", user, item);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findByStatusAndEndingIsBefore(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), item, user, Status.WAITING)));

        CommentDto comment1 = itemService.createComment(1, 1, CommentMapper.toCommentDto(comment));

        assertEquals(1, comment1.getId());
        assertEquals(comment.getUser().getName(), comment1.getAuthorName());
        verify(commentRepo).save(Mockito.any(Comment.class));
    }

    @Test
    void createCommentBadUserId() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "sdsd", user, item);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(ValidationException.class, () -> itemService.createComment(1, 1, CommentMapper.toCommentDto(comment)));
        assertEquals("Неверные данные", e.getMessage());
    }

    @Test
    void createCommentBadItemId() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "sdsd", user, item);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(ValidationException.class, () -> itemService.createComment(1, 1, CommentMapper.toCommentDto(comment)));
        assertEquals("Неверные данные", e.getMessage());
    }

    @Test
    void createCommentNotBooker() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "sdsd", user, item);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(ValidationException.class, () -> itemService.createComment(1, 1, CommentMapper.toCommentDto(comment)));
        assertEquals("Пользователь не оставлял запрос", e.getMessage());
    }
}