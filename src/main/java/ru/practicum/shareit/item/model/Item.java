package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
public class Item implements Serializable {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
    private User owner;
}
