package ru.practicum.shareit.item.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.interfaces.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item, User user) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        List<Item> matches = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                matches.add(item);
            }
        }
        if (matches.isEmpty()) {
            throw new ItemNotFoundException("Такой вещи не было найдено");
        }
        return matches;
    }

    @Override
    public List<Item> getAllItems(User user) {
        List<Item> itemsList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(user)) {
                itemsList.add(item);
            }
        }
        return itemsList;
    }
}
