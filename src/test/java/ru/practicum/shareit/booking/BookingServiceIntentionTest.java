package ru.practicum.shareit.booking;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ErrorStatusException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceIntentionTest {


    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = User.builder().name("fgfg").email("sdwe@mail.ru").id(2).build();
        userService.createUser(UserMapper.toUserDto(user2));

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        ItemDto item1 = itemService.createItem(ItemMapper.toItemDto(item), 1);


        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(2), LocalDateTime.now().plusSeconds(3), item, user2, Status.WAITING);

        BookingDto booking1 = bookingService.createBooking(BookingMapper.toBookingDto(booking), 2);
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getId(), booking1.getItem().getId());
        assertEquals(booking.getStarting(), booking1.getStart());
    }

    @Test
    void approveBooking() {
        BookingDto booking2 = bookingService.approveBooking(1, 1, true);
        assertEquals(booking2.getStatus(), Status.APPROVED);
    }

    @Test
    void getBooking() {
        BookingDto booking = bookingService.getBooking(1, 1);

        assertEquals(booking.getStatus(), Status.APPROVED);
        assertEquals(booking.getItem().getName(), "Sd");
        assertEquals(booking.getBooker().getName(), "fgfg");

    }

    @Test
    void getBookingLs() {
        List<BookingDto> bookings = bookingService.getBookingLs(2, "ALL", 0, 1);
        BookingDto booking = bookings.get(0);
        assertEquals(booking.getStatus(), Status.APPROVED);
        assertEquals(booking.getItem().getName(), "Sd");
        assertEquals(booking.getBooker().getName(), "fgfg");

    }

    @Test
    @Transactional
    void getBookingWrongId() {
        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBooking(1, 100));
        assertEquals(e.getMessage(), "Неверные входные данные");
    }

    @Test
    @Transactional
    void getBookingWrongState() {
        Exception e = assertThrows(ErrorStatusException.class, () -> bookingService.getBookingLs(2, "SDsdsd", 0, 1));
        assertEquals(e.getMessage(), null);
    }


}