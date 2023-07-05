package ru.practicum.shareit.user.storage.inMemory;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.Validator;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;
import java.lang.reflect.Field;
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
        idCounter++;
        user.setId(idCounter);
        users.put(idCounter, user);
        return user;
    }

    @Override
    public User updateUser(Long id, Map<String, Object> fields) {
        if (users.get(id) != null) {
            User userCopy = SerializationUtils.clone(users.get(id));
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(User.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, userCopy, value);
            });
            Validator.validationUserCheck(userCopy, users);
            users.put(id, userCopy);
            return userCopy;
        }
        throw new ValidationException("User с таким id не существует");
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
}
