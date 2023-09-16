package ru.practicum.shareit.item;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceInteractionTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Order(1)
    void createItem() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        userService.createUser(UserMapper.toUserDto(user));
        User user2 = User.builder().name("fgfg").email("ssdd@mail.ru").id(2).build();
        userService.createUser(UserMapper.toUserDto(user2));
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        ItemDto item1 = itemService.createItem(ItemMapper.toItemDto(item), 1);
        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.APPROVED);
        List<UserDto> users = userService.getAllUsers();
        bookingService.createBooking(BookingMapper.toBookingDto(booking), 2);
        assertEquals(1, item1.getId());
        assertEquals("sds", item1.getDescription());
    }

    @Test
    @Order(2)
    void patchItem() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        userService.createUser(UserMapper.toUserDto(user));
        Item item = Item.builder().id(1).name("Sssd").owner(user).available(true).build();
        ItemDto item1 = itemService.patchItem(ItemMapper.toItemDto(item), 1, 1);
        assertEquals(1, item1.getId());
        assertEquals("sds", item1.getDescription());
        assertEquals("Sssd", item1.getName());
    }

    @Test
    @Order(3)
    void getItem() {
        ItemDto item = itemService.getItem(1, 1);
        assertEquals(1, item.getId());
        assertEquals("sds", item.getDescription());
        assertEquals("Sssd", item.getName());
    }

    @Test
    @Order(4)
    void getItems() {
        List<ItemDto> items = itemService.getItems(1, 0, 1);
        ItemDto item = items.get(0);
        assertEquals(1, items.size());
        assertEquals(1, item.getId());
        assertEquals("sds", item.getDescription());
        assertEquals("Sssd", item.getName());
    }

    @Test
    @Order(5)
    void search() {
        List<ItemDto> items = itemService.search("s", 0, 1);
        ItemDto item = items.get(0);
        assertEquals(1, items.size());
        assertEquals(1, item.getId());
        assertEquals("sds", item.getDescription());
        assertEquals("Sssd", item.getName());
    }

    @Test
    @Transactional
    @Order(6)
    void createComment() throws InterruptedException {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "Sdsds", user, item);
        Thread.sleep(2000);
        CommentDto commentDto = itemService.createComment(2, 1, CommentMapper.toCommentDto(comment));
        assertEquals(1, commentDto.getId());
        assertEquals("Sdsds", commentDto.getText());
    }
}