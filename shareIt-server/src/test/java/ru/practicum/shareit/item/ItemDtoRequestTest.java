package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoRequestTest {
    @Autowired
    private JacksonTester<ItemDtoRequest> jacksonTester;

    @SneakyThrows
    @Test
    void itemDtoRequestTestCreationTest() {
        ItemDtoRequest itemCreateDto = ItemDtoRequest.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();

        JsonContent<ItemDtoRequest> content = jacksonTester.write(itemCreateDto);
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
        ItemDtoRequest itemDto = ItemDtoRequest.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();
        JsonContent<ItemDtoRequest> content = jacksonTester.write(itemDto);
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
