package ru.practicum.shareit.user.service.interfaces;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUser(Long id);

    List<UserResponseDto> getUsers();

    UserResponseDto createUser(UserRequestDto userDto);

    UserResponseDto updateUser(UserRequestDto userDto, Long id);

    void deleteUser(Long id);

    void deleteUsers();
}
