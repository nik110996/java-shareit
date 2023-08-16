package ru.practicum.shareit.user.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 0;

    @Override
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User createUser(User user) {
        checkEmailDoubling(user);
        idCounter++;
        user.setId(idCounter);
        users.put(idCounter, user);
        return user;
    }

    @Override
    public void updateUser(User user) {
        checkEmailDoubling(user);
        users.put(user.getId(), user);
    }

    @Override
    public User findUserById(long id) {
        return users.get(id);
    }

    @Override
    public void deleteUsers() {
        users.clear();
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    private void checkEmailDoubling(User user) {
        List<User> oldUsers = new ArrayList<>(users.values());
        oldUsers.forEach(oldUser -> {
            if (user.getEmail().equals(oldUser.getEmail()) && user.getId() != oldUser.getId()) {
                throw new ValidationException("Email уже зарегистрирован в системе");
            }
        });
    }
}
