package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createRequest(@Valid @RequestBody ItemRequestDtoRequest itemRequestDtoResponse,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'",
                "POST", "/requests", itemRequestDtoResponse, userId);
        ItemRequestDtoResponse itemRequestDto = itemRequestService.createRequest(itemRequestDtoResponse, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/requests", itemRequestDto);
        return itemRequestDto;
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/requests", userId);
        List<ItemRequestDtoResponse> itemRequestDtoResponses = itemRequestService.getRequests(userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/requests", itemRequestDtoResponses);
        return itemRequestDtoResponses;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0")
            @Min(0) Integer from,
            @Min(1) @RequestParam(required = false, defaultValue = "10")
            Integer size
    ) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {} и с параметрами from = {} и size = {}'",
                "GET", "/requests/all", userId, from, size);
        List<ItemRequestDtoResponse> itemRequestDtoResponses = itemRequestService.getRequests(userId, from, size);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/requests/all", itemRequestDtoResponses);
        return itemRequestDtoResponses;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/requests/" + requestId, userId);
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService.findRequestById(userId, requestId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}",
                "GET", "/requests/" + requestId, itemRequestDtoResponse);
        return itemRequestDtoResponse;
    }

}
