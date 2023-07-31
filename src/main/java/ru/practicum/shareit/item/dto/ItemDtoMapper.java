package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner()
        );
    }
//return userStorage.getUsers().stream().map(UserDtoMapper::toUserResponseDto).collect(Collectors.toList());
    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream().map(ItemDtoMapper::toItemDto).collect(Collectors.toList());
    }
}
