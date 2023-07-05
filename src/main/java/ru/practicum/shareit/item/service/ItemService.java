package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Validator;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.interfaces.ItemStorage;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto createItem(Item item, Long userId) {
        checkUserExisting(userId);
        Validator.validationItemCheck(item);
        return itemStorage.createItem(item, userStorage.findUserById(userId));
    }

    public ItemDto updateItem(Long id, Map<String, Object> fields, Long userId) {
        checkUserExisting(userId);
        if (itemStorage.getItem(id).getOwner().getId() != userId) {
            throw new UserNotFoundException("Пользователь обновляющий Item не является его владельцем");
        }
        return itemStorage.updateItem(id, fields);
    }

    public ItemDto getItem(Long itemId, Long userId) {
        checkUserExisting(userId);
        return itemStorage.getItem(itemId);
    }

    public List<ItemDto> getItemBySearch(String text, Long userId) {
        checkUserExisting(userId);
        return itemStorage.getItemsBySearch(text);
    }

    public List<ItemDto> getAllItems(Long userId) {
        checkUserExisting(userId);
        return itemStorage.getAllItems(userStorage.findUserById(userId));
    }

    private void checkUserExisting(Long userId) {
        if (userStorage.findUserById(userId) == null) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }
}
