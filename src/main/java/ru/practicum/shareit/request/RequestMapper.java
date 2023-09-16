package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@UtilityClass
public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemDto> itemDtos) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester())
                .created(request.getCreated())
                .description(request.getDescription())
                .items(itemDtos)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getCreated()
        );
    }
}
