package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.CloseBooking;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepo userRepository;
    private final CommentRepo commentRepo;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoObjectException("Пользователь не найден");
        }
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != 0) {
            if (requestRepository.findById(itemDto.getRequestId()).isPresent()) {
                item.setRequest(requestRepository.findById(itemDto.getRequestId()).get());
            }
        }
        check(item);
        item.setOwner(user.get());
        ItemDto itemDto1 = ItemMapper.toItemDto(itemRepository.save(item));
        if (item.getRequest() != null) {
            itemDto1.setRequestId(item.getRequest().getId());
        }
        return itemDto1;
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, int id, int ownerId) {
        Optional<Item> itemBase = itemRepository.findById(id);
        if (itemBase.isEmpty()) {
            throw new NoObjectException("Item не найден в базе");
        }
        if (itemBase.get().getOwner().getId() != ownerId) {
            throw new NoObjectException("Это не ваша вещь");
        }
        Item item = update(itemBase.get(), itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int id, int ownerId) {
        try {
            Item item;
            Optional<Item> itemOptional = itemRepository.findById(id);
            if (itemOptional.isEmpty()) {
                throw new NoObjectException("Неверные данные");
            } else {
                item = itemOptional.get();
            }
            List<Comment> comments = commentRepo.findByItem(item);

            List<Booking> bookings = bookingRepository.getByItemIdAndStartingIsBeforeAndStatus(item.getId(), LocalDateTime.now(), Status.APPROVED, PageRequest.of(0, 100, Sort.by("starting").descending()));
            if (bookings != null && bookings.size() > 0 && item.getOwner().getId() == ownerId) {
                item.setLastBooking(new CloseBooking(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
            }
            bookings = bookingRepository.getByItemIdAndStartingIsAfterAndStatus(item.getId(), LocalDateTime.now(), Status.APPROVED, PageRequest.of(0, 100, Sort.by("starting").ascending()));
            if (bookings != null && bookings.size() > 0 && item.getOwner().getId() == ownerId) {
                item.setNextBooking(new CloseBooking(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
            }

            item.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
            return ItemMapper.toItemDto(item);
        } catch (Exception e) {
            throw new NoObjectException(e.getMessage());
        }
    }

    @Override
    public List<ItemDto> getItems(int ownerId, int from, int size) {
        List<ItemDto> answer = new ArrayList<>();
        List<Item> ls = itemRepository.findAll();
        for (Item o : ls) {
            if (o.getOwner().getId() == ownerId) {
                List<Booking> bookings = bookingRepository.getByItemIdAndStartingIsBeforeAndStatus(o.getId(), LocalDateTime.now(), Status.APPROVED, PageRequest.of(from, size, Sort.by("starting").descending()));
                if (bookings != null && bookings.size() > 0) {
                    o.setLastBooking(new CloseBooking(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
                }
                bookings = bookingRepository.getByItemIdAndStartingIsAfterAndStatus(o.getId(), LocalDateTime.now(), Status.APPROVED, PageRequest.of(from, size, Sort.by("starting").ascending()));
                if (bookings != null && bookings.size() > 0) {
                    o.setNextBooking(new CloseBooking(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
                }
                answer.add(ItemMapper.toItemDto(o));
            }
        }
        return answer;
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        List<ItemDto> itemList = new ArrayList<>();
        if (text == null || text.isEmpty() || text.isBlank()) {
            return itemList;
        }
        List<Item> items = itemRepository.search(text, PageRequest.of(from, size));
        return items.stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }


    private void check(Item item) {
         if (item.getAvailable() == null) {
            throw new ValidationException("Available пустой");
        } else if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Name пустое");
        } else if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Description пустое");
        }
    }

    private Item update(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    public CommentDto createComment(int ownerId, int idItem, CommentDto commentDto) {
        Optional<User> user = userRepository.findById(ownerId);
        Optional<Item> item = itemRepository.findById(idItem);
        List<Booking> lsBooking = bookingRepository.findByStatusAndEndingIsBefore(Status.APPROVED, LocalDateTime.now(), PageRequest.of(0,100,Sort.by("ending").descending()));
        Booking booking = null;
        if (user.isEmpty() || item.isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("Неверные данные");
        }
        for (Booking o : lsBooking) {
            if (o.getBooker().getId() == user.get().getId() && o.getItem().getId() == idItem) {
                booking = o;
            }
        }
        if (booking == null || booking.getBooker().getId() != ownerId) {
            throw new ValidationException("Пользователь не оставлял запрос");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setUser(user.get());
        comment.setItem(item.get());
        commentRepo.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

}
