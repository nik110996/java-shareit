package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ItemDtoResponseTest {
    @Autowired
    private JacksonTester<ItemDtoResponse> jacksonTester;

    @SneakyThrows
    @Test
    void itemDtoResponseTestCreationTest() {
        ItemDtoResponse itemCreateDto = ItemDtoResponse.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();

        JsonContent<ItemDtoResponse> content = jacksonTester.write(itemCreateDto);
        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemCreateDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemCreateDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemCreateDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemCreateDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemCreateDto.getRequestId().intValue());
    }

    @SneakyThrows
    @Test
    void itemDtoResponseTest() {
        ItemDtoResponse itemDto = ItemDtoResponse.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();
        JsonContent<ItemDtoResponse> content = jacksonTester.write(itemDto);
        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
    }
}
