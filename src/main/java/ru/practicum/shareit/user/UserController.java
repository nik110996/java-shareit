package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> getUsers() {
        log.info("Пришел запрос / эндпоинт: '{} {}'", "GET", "/users");
        List<User> usersList = service.getUsers();
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/users", usersList);
        return usersList;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Пришел запрос / эндпоинт: '{} {}' с телом '{}", "POST", "/users", user);
        User savedUser = service.createUser(user);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/users", savedUser);
        return savedUser;
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody Map<String, Object> fields) {
        log.info("Пришел запрос / эндпоинт: '{} {}' с телом '{}", "PATCH", "/users" + id, fields);
        User updatedUser = service.updateUser(id, fields);
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
    public User getUser(@PathVariable long id) {
        log.info("Получен запрос / эндпоинт: '{} {}'", "GET", "/users/" + id);
        User user = service.getUser(id);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/users/" + id, user);
        return user;
    }
}

