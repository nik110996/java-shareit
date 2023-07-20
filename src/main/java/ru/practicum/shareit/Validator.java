package ru.practicum.shareit;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Validator {

    public static void validationUserCheck(User user, UserStorage storage) {
        List<User> oldUsers = storage.getUsers();
        checkEmailDoubling(user, oldUsers);
    }

    public static void validationUserCheck(User user, Map<Long, User> users) {
        List<User> oldUsers = new ArrayList<>(users.values());
        checkEmailDoubling(user, oldUsers);
    }

    public static void validationItemCheck(Item item) {
        if (!item.isAvailable()) {
            throw new ValidationException("Item должен быть доступен");
        }
    }

    private static void checkEmailDoubling(User user, List<User> oldUsers) {
        oldUsers.forEach(oldUser -> {
            if (user.getEmail().equals(oldUser.getEmail()) && user.getId() != oldUser.getId()) {
                throw new ValidationException("Email уже зарегистрирован в системе");
            }
        });
    }
}
