package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserRequestDto;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoResponse {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoResponse item;
    private UserRequestDto booker;
    private Status status;
}
