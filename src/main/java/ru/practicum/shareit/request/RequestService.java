package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(int ownerId, ItemRequestDto requestDto);

    List<ItemRequestDto> getRequests(int id);

    ItemRequestDto getRequestId(int ownerId, int id);

    List<ItemRequestDto> getRequestsAnother(int id, int from, int size);
}
