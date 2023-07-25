package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserResponseDto getUser(long id) {
        if (userStorage.findUserById(id) == null) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return UserDtoMapper.toUserResponseDto(userStorage.findUserById(id));
    }

    public List<UserResponseDto> getUsers() {
        List<UserResponseDto> users = new ArrayList<>();
        userStorage.getUsers().forEach(user -> users.add(UserDtoMapper.toUserResponseDto(user)));
        return users;
    }

    public UserResponseDto createUser(UserRequestDto userDto) {
        User user = UserDtoMapper.toUser(userDto);
        return UserDtoMapper.toUserResponseDto(userStorage.createUser(user));
    }

    public UserResponseDto updateUser(UserRequestDto userDto, Long id) {
        User user = SerializationUtils.clone(userStorage.findUserById(id));
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userDto.getId() + " not found");
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        userStorage.updateUser(user);
        return UserDtoMapper.toUserResponseDto(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void deleteUsers() {
        userStorage.deleteUsers();
    }
}
