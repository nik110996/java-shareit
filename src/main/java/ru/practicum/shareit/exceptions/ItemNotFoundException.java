package ru.practicum.shareit.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class ItemNotFoundException extends IllegalArgumentException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
