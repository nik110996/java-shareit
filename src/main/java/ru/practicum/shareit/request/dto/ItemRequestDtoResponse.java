package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private User requester;
    private List<Item> items;
    private LocalDateTime created;
}
