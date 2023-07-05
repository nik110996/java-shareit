package ru.practicum.shareit.item.storage.inMemory;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.interfaces.ItemStorage;
import ru.practicum.shareit.user.User;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryItemStorage")
public class inMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 0;

    @Override
    public ItemDto createItem(Item item, User user) {
        idCounter++;
        item.setId(idCounter);
        item.setOwner(user);
        items.put(idCounter, item);
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long id, Map<String, Object> fields) {
        if (items.get(id) != null) {
            Item item = items.get(id);
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Item.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, item, value);
            });
            return ItemDtoMapper.toItemDto(item);
        }
        throw new ValidationException("Item с таким id не существует");
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemDtoMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        List<ItemDto> matches = new ArrayList<>();
        if (text.isBlank()) {
            return matches;
        }
        for (Item item : items.values()) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.isAvailable()) {
                matches.add(ItemDtoMapper.toItemDto(item));
            }
        }
        if (matches.isEmpty()) {
            throw new ItemNotFoundException("Такой вещи не было найдено");
        }
        return matches;
    }

    @Override
    public List<ItemDto> getAllItems(User user) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == user) {
                itemDtoList.add(ItemDtoMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }
}
