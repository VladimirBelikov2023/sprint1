package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ErrorStatusException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    private BookingService bookingService;
    @Mock
    private UserRepo userRepo;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void serUp() {
        UserService userService = new UserServiceImpl(userRepo);
        bookingService = new BookingServiceImpl(itemRepository, bookingRepository, userRepo, userService);

    }

    @Test
    void createBooking() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        BookingDto booking1 = bookingService.createBooking(BookingMapper.toBookingDto(booking), 2);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStart());
    }

    @Test
    void createBookingUnavailable() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(false).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(ValidationException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Item не доступна", e.getMessage());
    }

    @Test
    void createBookingWrongTime() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, null, LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(ValidationException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Не верное время бронирования", e.getMessage());
    }

    @Test
    void createBookingBadUser() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Неверные входные данные", e.getMessage());

    }

    @Test
    void createBookingBadItemId() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Неверные входные данные", e.getMessage());
    }

    @Test
    void createBookingNotOwner() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));


        Exception e = assertThrows(NoObjectException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 1));
        assertEquals("Вы владелец вещи", e.getMessage());
    }

    @Test
    void createBookingBadStart() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));


        Exception e = assertThrows(ValidationException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Не верное время бронирования", e.getMessage());
    }

    @Test
    void createBookingBadEnd() {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now(), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(item));


        Exception e = assertThrows(ValidationException.class, () -> bookingService.createBooking(BookingMapper.toBookingDto(booking), 2));
        assertEquals("Не верное время бронирования", e.getMessage());
    }

    @Test
    void approveBooking() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        Booking booking1 = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.APPROVED);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingDto booking2 = bookingService.approveBooking(1, 1, true);
        assertEquals(booking2.getStatus(), Status.APPROVED);
    }

    @Test
    void approveBookingRegected() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        Booking booking1 = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.REJECTED);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingDto booking2 = bookingService.approveBooking(1, 1, false);
        assertEquals(booking2.getStatus(), Status.REJECTED);
    }

    @Test
    void approveBookingBadUser() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.approveBooking(1, 1, true));
        assertEquals("Неверные входные данные", e.getMessage());
    }

    @Test
    void approveBookingBadBookingId() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.approveBooking(1, 1, true));
        assertEquals("Неверные входные данные", e.getMessage());
    }

    @Test
    void approveBookingNotOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        Booking booking1 = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.APPROVED);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.approveBooking(1, 1, true));
        assertEquals("Вы не владелец", e.getMessage());
    }

    @Test
    void approveBookingAlreadyDone() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.APPROVED);
        Booking booking1 = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.APPROVED);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(ValidationException.class, () -> bookingService.approveBooking(1, 1, true));
        assertEquals("Запрос уже рассмотрен", e.getMessage());
    }

    @Test
    void getBooking() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));

        BookingDto booking1 = bookingService.getBooking(1, 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStart());
    }

    @Test
    void getBookingBadUserId() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBooking(1, 1));
        assertEquals("Неверные входные данные", e.getMessage());
    }

    @Test
    void getBookingBadBookingId() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBooking(1, 1));
        assertEquals("Неверные входные данные", e.getMessage());
    }

    @Test
    void getBookingNotAllowed() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBooking(1, 1));
        assertEquals("Вы не можете посмотреть запрос", e.getMessage());
    }

    @Test
    void getBookingLsBadUser() {
        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBookingLs(1, "WAITING", 0, 1));
        assertEquals("User не найден", e.getMessage());
    }

    @Test
    void getBookingLsBadState() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(ErrorStatusException.class, () -> bookingService.getBookingLs(1, "sds", 0, 1));
        assertNull(e.getMessage());
    }

    @Test
    void getBookingLsOwner() {
        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBookingLsOwner(1, "WAITING", 0, 1));
        assertEquals("User не найден", e.getMessage());
    }

    @Test
    void getBookingLsOwnerBadState() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));

        Exception e = assertThrows(ErrorStatusException.class, () -> bookingService.getBookingLsOwner(1, "sds", 0, 1));
        assertNull(e.getMessage());
    }

    @Test
    void getBookingAllBadSize() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));

        Exception e = assertThrows(ArithmeticException.class, () -> bookingService.getBookingAll(1, 0, 0, false));
        assertEquals("/ by zero", e.getMessage());
    }


    @Test
    void getBookingAll() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerId(Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingAll(1, 1, 1, false);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBookingAllOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllOrderedBookingOwnerPag(Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.getBookingAll(1, 1, 1, true);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBookingBadUser() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        Exception e = assertThrows(NoObjectException.class, () -> bookingService.getBookingAll(1, 0, 0, false));
        assertEquals("User не найден", e.getMessage());
    }

    @Test
    void getBooksWaiting() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByStatusAndBookerId(Mockito.any(), Mockito.anyInt(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "WAITING", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksWaitingOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();
        int ownerId = 2;

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllWaitingBookingOwner(Mockito.anyInt(), Mockito.any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "WAITING", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksAll() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByBookerId(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "ALL", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksAllOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllOrderedBookingOwner(Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "ALL", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }


    @Test
    void getBooksPast() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByEndingIsBeforeAndBookerId(Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "PAST", 1, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksPastOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllLastBookingOwner(Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "PAST", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksCurrent() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByStartingIsBeforeAndEndingIsAfterAndBookerId(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "CURRENT", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksCurrentOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllCurrentBookingOwner(Mockito.anyInt(), Mockito.any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "CURRENT", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksFuture() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByStartingIsAfterAndBookerIdOrderByEndingDesc(Mockito.any(LocalDateTime.class), Mockito.anyInt())).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "FUTURE", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksFutureOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllFutureBookingOwner(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "FUTURE", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }


    @Test
    void getBooksRejected() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findByBookerIdAndStatus(Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLs(1, "REJECTED", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksRejectedOwner() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllRejectedBookingOwner(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingLsOwner(1, "REJECTED", 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking.getStarting(), bookings.get(0).getStart());
    }

    @Test
    void getBooksWrongStatus() {
        User user = User.builder().name("fgfg").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("fgfg").email("sdsd@mail.ru").id(2).build();

        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);
        when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user2));

        Exception e = assertThrows(ErrorStatusException.class, () -> bookingService.getBookingLs(1, "REJECTEdsdD", 0, 1));
        assertNull(e.getMessage());
    }


}