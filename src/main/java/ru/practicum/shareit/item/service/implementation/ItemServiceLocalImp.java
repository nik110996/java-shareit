package ru.practicum.shareit.item.service.implementation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.item.storage.interfaces.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemServiceLocalImp implements ItemService {
    private long idCounter = 0;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Item item, Long userId) {
        User user = userStorage.findUserById(userId);
        checkUserExisting(user);
        if (!item.getAvailable()) {
            throw new ValidationException("Item должен быть доступен");
        }
        idCounter++;
        item.setId(idCounter);
        item.setOwner(user);
        return ItemDtoMapper.toItemDto(itemStorage.createItem(item, user));
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Long userId) {
        checkUserExisting(userStorage.findUserById(userId));
        Item item = SerializationUtils.clone(itemStorage.getItem(id));
        if (item == null || item.getOwner().getId() != userId) {
            throw new ItemNotFoundException("Item with id = " + itemDto.getId() + " not found");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemStorage.updateItem(item);
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        checkUserExisting(userStorage.findUserById(userId));
        return ItemDtoMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, Long userId) {
        checkUserExisting(userStorage.findUserById(userId));
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemStorage.getItemsBySearch(text).forEach(item -> itemDtoList.add(ItemDtoMapper.toItemDto(item)));
        return itemDtoList;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        User user = userStorage.findUserById(userId);
        checkUserExisting(user);
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemStorage.getAllItems(user).forEach(
                item -> itemDtoList.add(ItemDtoMapper.toItemDto(item)));
        return itemDtoList;
    }

    private void checkUserExisting(User user) {
        if (user == null) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }
}
