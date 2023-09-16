package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(int ownerId, ItemRequestDto requestDto) {
        User user = UserMapper.toUser(userService.getUser(ownerId));
        requestDto.setRequester(user);
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new ValidationException("Не верное описание");
        }
        ItemRequest itemRequest = RequestMapper.toItemRequest(requestDto);
        return RequestMapper.toItemRequestDto(requestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getRequests(int id) {
        User user = UserMapper.toUser(userService.getUser(id));
        List<ItemRequestDto> answer = new ArrayList<>();
        List<ItemRequest> itemRequests = requestRepository.getByRequesterId(id, Sort.by("created").descending());
        for (ItemRequest o : itemRequests) {
            List<ItemDto> items = new ArrayList<>();
            List<Item> lsItems = itemRepository.getByRequestId(o.getId());
            for (Item j : lsItems) {
                items.add(ItemMapper.toItemDto(j));
            }
            answer.add(RequestMapper.toItemRequestDto(o, items));
        }
        return answer;
    }

    @Override
    public ItemRequestDto getRequestId(int ownerId, int id) {
        User user = UserMapper.toUser(userService.getUser(ownerId));
        ItemRequest itemRequest;
        Optional<ItemRequest> itemRequestsOpt = requestRepository.findById(id);
        if (itemRequestsOpt.isPresent()) {
            itemRequest = itemRequestsOpt.get();
        } else {
            throw new NoObjectException("Запрос не найден");
        }
        List<ItemDto> items = new ArrayList<>();
        List<Item> itemList = itemRepository.getByRequestId(itemRequest.getId());
        for (Item o : itemList) {
            items.add(ItemMapper.toItemDto(o));
        }
        return RequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getRequestsAnother(int id, int from, int size) {
        UserDto userDto = userService.getUser(id);
        List<ItemRequest> itemRequests = requestRepository.getAllByAnother(id, PageRequest.of(from, size, Sort.by("created").descending()));
        List<ItemRequestDto> answer = new ArrayList<>();
        for (ItemRequest o : itemRequests) {
            List<Item> itemList = itemRepository.getByRequestId(o.getId());
            List<ItemDto> items = new ArrayList<>();
            for (Item j : itemList) {
                items.add(ItemMapper.toItemDto(j));
            }
            answer.add(RequestMapper.toItemRequestDto(o,items));
        }
        return answer;
    }
}
