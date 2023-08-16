package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

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

    public static User toUser(UserRequestDto userRequestDto) {
        return new User(
                userRequestDto.getId(),
                userRequestDto.getName(),
                userRequestDto.getEmail()
        );
    }
}
