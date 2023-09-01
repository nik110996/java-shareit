package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDtoMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .items(itemRequest.getItems())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDtoRequest toItemRequestDtoRequest(ItemRequestDtoResponse itemRequestDtoResponse) {
        return ItemRequestDtoRequest.builder()
                .description(itemRequestDtoResponse.getDescription())
                .build();
    }

}
