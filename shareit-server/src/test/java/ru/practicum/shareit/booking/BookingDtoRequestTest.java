package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoRequestTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> jacksonTester;

    @SneakyThrows
    @Test
    void bookingDtoRequestTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        JsonContent<BookingDtoRequest> content = jacksonTester.write(bookingDto);
        assertThat(content).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());
    }
}

