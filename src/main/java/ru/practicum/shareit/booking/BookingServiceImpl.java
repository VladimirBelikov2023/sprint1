package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ErrorStatusException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepo;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepo userRepo;
    private final UserService userService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, int ownerId) {
        Item item;
        User user;
        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        if (itemOptional.isEmpty() || ownerOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            item = itemOptional.get();
            user = ownerOptional.get();
        }
        if (item.getOwner().getId() == ownerId) {
            throw new NoObjectException("Вы владелец вещи");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        check(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    public BookingDto approveBooking(int ownerId, int id, boolean isApproved) {
        User owner;
        Booking booking;
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (ownerOptional.isEmpty() || bookingOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            owner = ownerOptional.get();
            booking = bookingOptional.get();
        }
        if (booking.getItem().getOwner().getId() != owner.getId()) {
            throw new NoObjectException("Вы не владелец");
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new ValidationException("Запрос уже рассмотрен");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }


    public BookingDto getBooking(int ownerId, int id) {
        User owner;
        Booking booking;
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (ownerOptional.isEmpty() || bookingOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            owner = ownerOptional.get();
            booking = bookingOptional.get();
        }
        if (booking.getBooker().getId() != owner.getId() && owner.getId() != booking.getItem().getOwner().getId()) {
            throw new NoObjectException("Вы не можете посмотреть запрос");
        }
        return BookingMapper.toBookingDto(booking);

    }


    public List<BookingDto> getBookingLs(int ownerId, String state, int from, int size) {
        Optional<User> owner = userRepo.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NoObjectException("User не найден");
        }
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by("ending").descending());
        return getBooks(state, ownerId, false, pageRequest);
    }

    public List<BookingDto> getBookingLsOwner(int ownerId, String state, int from, int size) {
        Optional<User> owner = userRepo.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NoObjectException("User не найден");
        }
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by("ending").descending());
        return getBooks(state, ownerId, true, pageRequest);
    }


    private List<BookingDto> getBooks(String stateStr, int ownerId, boolean isTrue, PageRequest pageRequest) {
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (Exception e) {
            throw new ErrorStatusException();
        }
        List<BookingDto> answer = new ArrayList<>();
        List<Booking> bookings;
        switch (state) {
            case PAST:
                if (isTrue) {
                    bookings = bookingRepository.findAllLastBookingOwner(ownerId, pageRequest);
                } else {
                    bookings = bookingRepository.findByEndingIsBeforeAndBookerId(LocalDateTime.now(), ownerId, pageRequest);
                }
                break;
            case CURRENT:
                if (isTrue) {
                    bookings = bookingRepository.findAllCurrentBookingOwner(ownerId, Sort.by("ending").descending());
                } else {
                    bookings = bookingRepository.findByStartingIsBeforeAndEndingIsAfterAndBookerId(LocalDateTime.now(), LocalDateTime.now(), ownerId, pageRequest);
                }
                break;
            case FUTURE:
                if (isTrue) {
                    bookings = bookingRepository.findAllFutureBookingOwner(ownerId, pageRequest);
                } else {
                    bookings = bookingRepository.findByStartingIsAfterAndBookerIdOrderByEndingDesc(LocalDateTime.now(), ownerId);
                }
                break;
            case WAITING:
                if (isTrue) {
                    bookings = bookingRepository.findAllWaitingBookingOwner(ownerId, Sort.by("ending").descending());
                } else {
                    bookings = bookingRepository.findByStatusAndBookerId(Status.WAITING, ownerId,pageRequest);
                }
                break;
            case REJECTED:
                if (isTrue) {
                    bookings = bookingRepository.findAllRejectedBookingOwner(ownerId, pageRequest);
                } else {
                    bookings = bookingRepository.findByBookerIdAndStatus(ownerId, Status.REJECTED, pageRequest);
                }
                break;
            default:
                if (isTrue) {
                    bookings = bookingRepository.findAllOrderedBookingOwner(ownerId, pageRequest);
                } else {
                    bookings = bookingRepository.findByBookerId(ownerId, pageRequest);
                }
                break;
        }
        for (Booking o : bookings) {
            answer.add(BookingMapper.toBookingDto(o));
        }
        return answer;
    }

    public List<BookingDto> getBookingAll(int ownerId, int from, int size, boolean isTrue) {
        User owner = UserMapper.toUser(userService.getUser(ownerId));
        List<Booking> bookings;
        if (!isTrue) {
            bookings = bookingRepository.findByBookerId(ownerId, PageRequest.of(from / size, size, Sort.by("ending").descending()));
        } else {
            bookings = bookingRepository.findAllOrderedBookingOwnerPag(ownerId, PageRequest.of(from / size, size, Sort.by("ending").descending()));
        }
        List<BookingDto> answer = new ArrayList<>();
        for (Booking o : bookings) {
            BookingMapper.toBookingDto(o);
            answer.add(BookingMapper.toBookingDto(o));
        }
        return answer;
    }


    private void check(BookingDto bookingDto) {
        if (!bookingDto.getItem().getAvailable()) {
            throw new ValidationException("Item не доступна");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Не верное время бронирования");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("Не верное время бронирования");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Не верное время бронирования");
        }
    }
}
