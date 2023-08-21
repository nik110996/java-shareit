package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDtoRequest {
        private Long itemId;
        @FutureOrPresent
        @NotNull
        private LocalDateTime start;
        @Future
        @NotNull
        private LocalDateTime end;
}

