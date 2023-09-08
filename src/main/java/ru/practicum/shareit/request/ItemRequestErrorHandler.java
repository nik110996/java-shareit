package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit.request")
public class ItemRequestErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final ItemRequestNotFoundException e) {
        return Map.of("Ошибка /items", e.getMessage());
    }
}
