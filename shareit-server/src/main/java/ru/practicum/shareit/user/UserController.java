package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "GET", "/users");
        List<UserResponseDto> usersList = service.getUsers();
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/users", usersList);
        return usersList;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto user) {
        log.info("Пришел запрос / эндпоинт: '{} {}' с телом '{}", "POST", "/users", user);
        UserResponseDto savedUser = service.createUser(user);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/users", savedUser);
        return savedUser;
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserRequestDto userDto) {
        log.info("Пришел запрос / эндпоинт: '{} {}' с телом '{}", "PATCH", "/users" + id, userDto);
        UserResponseDto updatedUser = service.updateUser(userDto, id);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "PATCH", "/users" + id, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "DELETE", "/users/" + id);
        service.deleteUser(id);
        log.info("Получен ответ / эндпоинт: '{} {}'", "DELETE", "/users/" + id);
    }

    @DeleteMapping
    public void deleteUsers() {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "DELETE", "/users");
        service.deleteUsers();
        log.info("Получен ответ / эндпоинт: '{} {}'", "DELETE", "/users");
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable long id) {
        log.info("Получен запрос / эндпоинт: '{} {}'", "GET", "/users/" + id);
        UserResponseDto user = service.getUser(id);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/users/" + id, user);
        return user;
    }
}

