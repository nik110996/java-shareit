package ru.practicum.shareit.request.service.interfaces;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createRequest(ItemRequestDtoRequest requestDto, Long userId);

    List<ItemRequestDtoResponse> getRequests(Long userId);

    List<ItemRequestDtoResponse> getRequests(Long userid, Integer from, Integer size);

    ItemRequestDtoResponse findRequestById(Long userId, Long requestId);
}
