package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testUserCreateDto() {
        UserRequestDto userCreateDto = UserRequestDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();
        JsonContent<UserRequestDto> content = jacksonTester.write(userCreateDto);
        Assertions.assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(userCreateDto.getName());
        Assertions.assertThat(content).extractingJsonPathStringValue("$.email")
                .isEqualTo(userCreateDto.getEmail());
    }

    @SneakyThrows
    @Test
    void testUserDto() {
        UserRequestDto userDto = UserRequestDto.builder()
                .id(0L)
                .name("name")
                .email("email@email.ru")
                .build();
        JsonContent<UserRequestDto> content = jacksonTester.write(userDto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
