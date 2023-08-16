package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBC;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Item item, Long userId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long userId);

    ItemDtoBC getItem(Long itemId, Long userId);

    List<ItemDto> getItemBySearch(String text, Long userId);

    List<ItemDtoBC> getAllItems(Long userId);

    CommentDto createComment(Long userId, Long itemId, CommentDto comment);
}
