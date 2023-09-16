package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBook(@RequestHeader("X-Sharer-User-Id") int id, @RequestBody @Validated(Create.class) BookingDto booking) {
        return bookingService.createBooking(booking, id);
    }

    @PatchMapping("/{id}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") int owner, @RequestParam() boolean approved, @PathVariable int id) {
        return bookingService.approveBooking(owner, id, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") int owner, @PathVariable int bookingId) {
        return bookingService.getBooking(owner, bookingId);
    }


    @GetMapping
    public List<BookingDto> getBookingAll(@RequestHeader("X-Sharer-User-Id") int owner,  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from, @RequestParam(required = false, defaultValue = "1000")  @Positive int size, @RequestParam(required = false, defaultValue = "ALL") String state) {
        if (!state.equals("ALL")) {
            return bookingService.getBookingLs(owner, state, from, size);
        }
        return bookingService.getBookingAll(owner, from, size, false);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingAllOwner(@RequestHeader("X-Sharer-User-Id") int owner, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from, @RequestParam(required = false, defaultValue = "1000") @Positive int size, @RequestParam(required = false, defaultValue = "ALL") String state) {
        if (!state.equals("ALL")) {
            return bookingService.getBookingLsOwner(owner, state, from, size);
        }
        return bookingService.getBookingAll(owner, from, size, true);
    }
}
