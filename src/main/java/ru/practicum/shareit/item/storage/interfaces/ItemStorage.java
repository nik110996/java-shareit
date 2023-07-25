package ru.practicum.shareit.item.storage.interfaces;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, User user);

    void updateItem(Item item);

    Item getItem(Long itemId);

    List<Item> getItemsBySearch(String text);

    List<Item> getAllItems(User user);
}
