package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, int ownerId);

    BookingDto approveBooking(int owner, int id, boolean isApproved);

    BookingDto getBooking(int owner, int id);

    List<BookingDto> getBookingLs(int ownerId, String state, int from, int size);

    List<BookingDto> getBookingLsOwner(int ownerId, String state, int from, int size);

    List<BookingDto> getBookingAll(int ownerId, int from, int size, boolean isTrue);
}
