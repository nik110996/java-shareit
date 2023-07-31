package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {

    ItemDto createItem(Item item, Long userId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItemBySearch(String text, Long userId);

    List<ItemDto> getAllItems(Long userId);
}
