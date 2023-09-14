package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ItemRequestDtoMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        if (itemRequest.getItems() != null) {
            itemRequestDtoResponse.setItems(itemRequest.getItems().stream()
                    .map(ItemDtoMapper::toItemDtoResponse).collect(Collectors.toList()));
        }
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());
        return itemRequestDtoResponse;
    }

    public static ItemRequestDtoRequest toItemRequestDtoRequest(ItemRequestDtoResponse itemRequestDtoResponse) {
        return ItemRequestDtoRequest.builder()
                .description(itemRequestDtoResponse.getDescription())
                .build();
    }

}
