package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, int id);

    ItemDto patchItem(ItemDto item, int id, int ownerId);

    ItemDto getItem(int id, int ownerId);

    List<ItemDto> getItems(int ownerId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto createComment(int ownerId, int idItem, CommentDto comment);
}
