package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'", "POST", "/items", item, userId);
        ItemDto itemDto = itemService.createItem(item, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/items", itemDto);
        return itemDto;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Long id, @Valid @RequestBody Map<String, Object> fields,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} с заголовком {}'",
                "PATCH", "/items" + id, fields, userId);
        ItemDto itemDto = itemService.updateItem(id, fields, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "PATCH", "/items", itemDto);
        return itemDto;
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/items/" + id, userId);
        ItemDto itemDto = itemService.getItem(id, userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/items/"
                + id, itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearch(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/items/search", userId, text);
        List<ItemDto> matches = itemService.getItemBySearch(text, userId);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/users/search", matches);
        return matches;
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "GET", "/items с заголовком " + userId);
        List<ItemDto> itemDtoList = itemService.getAllItems(userId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/items", itemDtoList);
        return itemDtoList;
    }
}
