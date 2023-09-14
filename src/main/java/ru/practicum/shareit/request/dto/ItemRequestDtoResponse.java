package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private List<ItemDtoResponse> items;
    private LocalDateTime created;
}
