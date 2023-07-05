package ru.practicum.shareit.item.storage.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.util.List;
import java.util.Map;

public interface ItemStorage {
    ItemDto createItem(Item item, User user);

    ItemDto updateItem(Long id, Map<String, Object> fields);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItemsBySearch(String text);

    List<ItemDto> getAllItems(User user);
}
