package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    long id;
    String description;
    User requester;
    LocalDateTime created;
}
