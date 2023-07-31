package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDtoMapper {

    public static UserRequestDto toUserRequestDto(User user) {
        return new UserRequestDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserResponseDto toUserResponseDto(UserRequestDto user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserResponseDto> toUserResponseDto(List<User> users) {
        List<UserResponseDto> responseDtos = new ArrayList<>();
        users.forEach(user -> responseDtos.add(toUserResponseDto(user)));
        return responseDtos;
    }

    public static User toUser(UserRequestDto userRequestDto) {
        return new User(
                userRequestDto.getId(),
                userRequestDto.getName(),
                userRequestDto.getEmail()
        );
    }

    public static User toUser(UserResponseDto userResponseDto) {
        return new User(
                userResponseDto.getId(),
                userResponseDto.getName(),
                userResponseDto.getEmail()
        );
    }
}
