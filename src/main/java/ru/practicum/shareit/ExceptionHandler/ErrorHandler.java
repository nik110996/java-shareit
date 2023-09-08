package ru.practicum.shareit.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e) {
        return Map.of("Ошибка UserNotFoundException - 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final ItemNotFoundException e) {
        return Map.of("Ошибка ItemNotFoundException - 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final ItemRequestNotFoundException e) {
        return Map.of("Ошибка ItemRequestNotFoundException - 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFound(final ValidationException e) {
        log.error("Ошибка ValidationException - 400");
        return new ErrorResponse(String.format(e.getMessage()));
    }

    /*@ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUserNotFound(final ValidationException e) {
        return Map.of("Ошибка /users", e.getMessage());
    }*/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("500 — Произошла непредвиденная ошибка.");
        return new ErrorResponse(
                String.format("Непредвиденная ошибка: " + e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingNotFound(final BookingNotFoundException e) {
        return Map.of("Ошибка BookingNotFoundException - 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleValidationException(final MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Поля класса не прошли валидацию");
    }
}
