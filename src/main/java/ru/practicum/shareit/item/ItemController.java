package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBC;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse createItem(@Valid @RequestBody ItemDtoRequest item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'", "POST", "/items", item, userId);
        ItemDtoResponse itemDto = itemService.createItem(item, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/items", itemDto);
        return itemDto;
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@PathVariable Long id, @RequestBody ItemDtoResponse itemDto,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} с заголовком {}'",
                "PATCH", "/items/" + id, itemDto, userId);
        ItemDtoResponse updatedItemDto = itemService.updateItem(id, itemDto, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "PATCH", "/items", updatedItemDto);
        return updatedItemDto;
    }

    @GetMapping("/{id}")
    public ItemDtoBC getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/items/" + id, userId);
        ItemDtoBC itemDto = itemService.getItem(id, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/items/"
                + id, itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemBySearch(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0", required = false) @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "10", required = false) @Min(1) Integer size) {
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/items/search", userId, text);
        List<ItemDtoResponse> matches = itemService.getItemBySearch(text, userId, from, size);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/users/search", matches);
        return matches;
    }

    @GetMapping
    public List<ItemDtoBC> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "0", required = false) @Min(0) Integer from,
                                       @RequestParam(defaultValue = "10", required = false) @Min(1) Integer size) {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "GET", "/items с заголовком " + userId);
        List<ItemDtoBC> itemDtoList = itemService.getAllItems(userId, from, size);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/items", itemDtoList);
        return itemDtoList;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId, @Valid @RequestBody CommentDto comment) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'", "POST",
                "/items/" + itemId + "/comment", comment, userId);
        CommentDto commentDto = itemService.createComment(userId, itemId, comment);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/items/" + itemId + "/comment", comment);
        return commentDto;
    }
}
