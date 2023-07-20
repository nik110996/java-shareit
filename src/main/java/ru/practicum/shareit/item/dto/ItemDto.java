package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
}
