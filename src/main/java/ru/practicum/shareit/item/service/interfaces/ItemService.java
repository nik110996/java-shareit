package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBC;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {

    ItemDtoResponse createItem(ItemDtoRequest item, Long userId);

    ItemDtoResponse updateItem(Long id, ItemDtoResponse itemDto, Long userId);

    ItemDtoBC getItem(Long itemId, Long userId);

    List<ItemDtoResponse> getItemBySearch(String text, Long userId, Integer from, Integer size);

    List<ItemDtoBC> getAllItems(Long userId, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto comment);
}
