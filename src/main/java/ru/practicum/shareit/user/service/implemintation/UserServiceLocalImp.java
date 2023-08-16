package ru.practicum.shareit.user.service.implemintation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;
import ru.practicum.shareit.user.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component("userServiceLocalImp")
@Service
@RequiredArgsConstructor
public class UserServiceLocalImp implements UserService {

    private final UserStorage userStorage;
    private long idCounter = 0;

    @Override
    public UserResponseDto getUser(Long id) {
        if (userStorage.findUserById(id) == null) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return UserDtoMapper.toUserResponseDto(userStorage.findUserById(id));
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userStorage.getUsers().stream().map(UserDtoMapper::toUserResponseDto).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userDto) {
        User user = UserDtoMapper.toUser(userDto);
        idCounter++;
        user.setId(idCounter);
        return UserDtoMapper.toUserResponseDto(userStorage.createUser(user));
    }

    @Override
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

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public void deleteUsers() {
        userStorage.deleteUsers();
    }
}
