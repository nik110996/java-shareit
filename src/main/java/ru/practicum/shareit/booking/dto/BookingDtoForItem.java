package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoForItem {
    private Long id;
    private Item item;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
