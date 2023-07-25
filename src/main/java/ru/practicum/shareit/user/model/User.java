package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder
@Data
public class User implements Serializable {
    private long id;
    private String name;
    @NotBlank(message = "Пустая электронная почта")
    @Email(message = "Некорректная почта")
    private String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
