package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemDtoMapper {

    public static ItemDtoResponse toItemDto(Item item) {
        ItemDtoResponse itemDto = new ItemDtoResponse();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
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

    public static ItemDtoBC toItemDtoBC(Item item) {
        return new ItemDtoBC(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static ItemDtoResponse toItemDtoResponse(Item item) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDtoResponse.setRequestId(item.getRequest().getId());
        }
        return itemDtoResponse;
    }

    public static ItemDtoResponse toItemDtoResponse(ItemDtoRequest item) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailable(item.getAvailable());
        return itemDtoResponse;
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, User owner) {
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .owner(owner)
                .build();
    }

    public static Item toItem(ItemDtoResponse itemDtoResponse, User owner) {
        return Item.builder()
                .id(itemDtoResponse.getId())
                .name(itemDtoResponse.getName())
                .description(itemDtoResponse.getDescription())
                .available(itemDtoResponse.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemDtoRequest toItemRequest(ItemDtoResponse itemDtoResponse) {
        return new ItemDtoRequest(itemDtoResponse.getName(), itemDtoResponse.getDescription(),
                itemDtoResponse.getAvailable(), itemDtoResponse.getRequestId());
    }
}
