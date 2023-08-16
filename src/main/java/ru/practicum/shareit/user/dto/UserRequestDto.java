package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class UserRequestDto {
    @NotNull
    private long id;
    private String name;
    @NotBlank(message = "Пустая электронная почта")
    @Email(message = "Некорректная почта")
    private String email;
}
