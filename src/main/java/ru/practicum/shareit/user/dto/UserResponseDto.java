package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserResponseDto {
    private long id;
    private String name;
    @NotBlank(message = "Пустая электронная почта")
    @Email(message = "Некорректная почта")
    private String email;
}
