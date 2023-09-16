package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createBook() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user, Status.WAITING);

        when(bookingService.createBooking(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingDto bookingDto = invocationOnMock.getArgument(0, BookingDto.class);
                    return bookingDto;
                });

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void createBookBadItems() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Booking booking = new Booking(1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), null, user, Status.WAITING);

        when(bookingService.createBooking(Mockito.any(BookingDto.class), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Item"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }


    @Test
    void approveBooking() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("sds").email("sfddd@mail.ru").id(2).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        when(bookingService.approveBooking(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    boolean isTrue = invocationOnMock.getArgument(2, Boolean.class);
                    if (isTrue) {
                        booking.setStatus(Status.APPROVED);
                    } else {
                        booking.setStatus(Status.REJECTED);
                    }
                    return booking;
                });

        mvc.perform(patch("/bookings/{id}", 1)
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void getBooking() throws Exception {

        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user, Status.WAITING);

        when(bookingService.getBooking(Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> booking);

        mvc.perform(get("/bookings/{id}", 1)
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void getBookingAll() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("sds").email("sfddd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        when(bookingService.getBookingAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenAnswer(invocationOnMock -> List.of(booking));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(List.of(booking)))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void getBookingAllOwner() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("sds").email("sfddd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        when(bookingService.getBookingLsOwner(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(List.of(booking)))
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void getBookingAll2() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("sds").email("sfddd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        when(bookingService.getBookingAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenAnswer(invocationOnMock -> List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(List.of(booking)))
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }


    @Test
    void getBookingAll3() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        User user2 = User.builder().name("sds").email("sfddd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        BookingDto booking = new BookingDto(1, 1, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), item, user2, Status.WAITING);

        when(bookingService.getBookingLs(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(booking));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(List.of(booking)))
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }
}