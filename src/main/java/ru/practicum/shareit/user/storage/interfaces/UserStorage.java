package ru.practicum.shareit.user.storage.interfaces;

import ru.practicum.shareit.user.User;
import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User user);

    User updateUser(Long id, Map<String, Object> fields);

    User findUserById(long id);

    void deleteUsers();

    void deleteUser(Long id);
}
