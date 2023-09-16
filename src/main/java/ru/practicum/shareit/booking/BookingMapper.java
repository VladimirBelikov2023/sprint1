package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStarting())
                .end(booking.getEnding())
                .itemId(booking.getItem().getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus()).build();
    }


    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .starting(bookingDto.getStart())
                .ending(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus()).build();
    }
}
