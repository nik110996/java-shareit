package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @SneakyThrows
    @Test
    void commentDtoInTest() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName("author")
                .build();
        JsonContent<CommentDto> content = jacksonTester.write(commentDto);
        assertThat(content).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(content).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }

    @SneakyThrows
    @Test
    void CommentDtoOutTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(0L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.parse("2023-07-20T22:03:23.909930411"))
                .build();

        JsonContent<CommentDto> content = jacksonTester.write(commentDto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(content).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
    }
}
