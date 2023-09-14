package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User user);

    void updateUser(User user);

    User findUserById(long id);

    void deleteUsers();

    void deleteUser(Long id);
}
