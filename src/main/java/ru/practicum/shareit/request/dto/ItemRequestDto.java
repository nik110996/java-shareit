package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
/**
 * TODO Sprint add-item-requests.
 */

@Builder
@Data
public class ItemRequestDto {
    String description;
    User requester;
    LocalDateTime created;
}
