package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoBC toItemDtoBC(Item item, BookingDtoForItem lastBooking,
                                        BookingDtoForItem nextBooking, List<CommentDto> comments) {
        return new ItemDtoBC(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }
}
