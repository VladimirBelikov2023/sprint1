package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @DirtiesContext
    @Test
    void getByItemIdAndStartingIsBeforeAndStatusTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(2), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getByItemIdAndStartingIsBeforeAndStatus(item.getId(), LocalDateTime.now().plusHours(1), Status.WAITING, PageRequest.of(0, 1));
        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void getByItemIdAndStartingIsAfterAndStatusTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(2), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getByItemIdAndStartingIsAfterAndStatus(item.getId(), LocalDateTime.now(), Status.WAITING, PageRequest.of(0, 1));
        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByEndingIsBeforeAndBookerIdTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(2), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByEndingIsBeforeAndBookerId(LocalDateTime.now().plusHours(4), user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));
        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllLastBookingOwnerTest() throws InterruptedException {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(1), item, user, Status.WAITING);
        bookingRepository.save(booking);
        Thread.sleep(1000);

        List<Booking> bookings = bookingRepository.findAllLastBookingOwner(user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByStartingIsAfterAndBookerIdOrderByEndingDescTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(1), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStartingIsAfterAndBookerIdOrderByEndingDesc(LocalDateTime.now(), user.getId());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllFutureBookingOwnerTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(1), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllFutureBookingOwner(user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByStartingIsBeforeAndEndingIsAfterAndBookerId() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStartingIsBeforeAndEndingIsAfterAndBookerId(LocalDateTime.now().plusSeconds(2), LocalDateTime.now().plusSeconds(3), user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByStatusAndEndingIsBefore() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStatusAndEndingIsBefore(Status.WAITING, LocalDateTime.now().plusSeconds(5), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllCurrentBookingOwner() throws InterruptedException {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);
        Thread.sleep(1000);

        List<Booking> bookings = bookingRepository.findAllCurrentBookingOwner(user.getId(), Sort.by("ending").descending());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByStatusAndBookerId() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStatusAndBookerId(Status.WAITING, user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllWaitingBookingOwner() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllWaitingBookingOwner(user.getId(), Sort.by("ending").descending());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());

    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStatus() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.WAITING);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), Status.WAITING, PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllRejectedBookingOwner() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.REJECTED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllRejectedBookingOwner(user.getId(), PageRequest.of(0, 1, Sort.by("ending").descending()));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByBookerId() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.REJECTED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerId(user.getId(), Sort.by("ending").descending());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllOrderedBookingOwnerPag() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.REJECTED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllOrderedBookingOwnerPag(user.getId(), PageRequest.of(0, 1));

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findAllOrderedBookingOwner() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.REJECTED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerId(user.getId(), Sort.by("ending").descending());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());
    }

    @DirtiesContext
    @Test
    void findByBookerIdTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(4), item, user, Status.REJECTED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerId(user.getId(), Sort.by("ending").descending());

        Booking booking1 = bookings.get(0);
        assertEquals(bookings.size(), 1);
        assertEquals(booking.getId(), booking1.getId());
        assertEquals(booking.getStatus(), booking1.getStatus());
        assertEquals(booking.getItem().getName(), booking1.getItem().getName());
        assertEquals(booking.getStarting(), booking1.getStarting());

    }
}