package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Validator;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User getUser(long id) {
        if (userStorage.findUserById(id) == null) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return userStorage.findUserById(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        Validator.validationUserCheck(user, userStorage);
        return userStorage.createUser(user);
    }

    public User updateUser(Long id, Map<String, Object> fields) {
        return userStorage.updateUser(id, fields);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void deleteUsers() {
        userStorage.deleteUsers();
    }
}
