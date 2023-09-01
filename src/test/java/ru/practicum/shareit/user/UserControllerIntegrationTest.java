package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.interfaces.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private Long userId;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        userRequestDto = UserRequestDto.builder()
                .id(1L)
                .name("Name")
                .email("user@email.ru").build();
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("Name")
                .email("user@email.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void createWhenInvokeThenInvokeUserService() {
        when(userService.createUser(userRequestDto)).thenReturn(userResponseDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userRequestDto), result);
        verify(userService).createUser(userRequestDto);
    }

   @SneakyThrows
   @Test
   void findByIdWhenInvokeThenInvokeUserService() {
       mockMvc.perform(get("/users/{userId}", userId))
               .andExpect(status().isOk());
       verify(userService).getUser(userId);
   }

    @SneakyThrows
    @Test
    void findAllUsersTest() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userService).getUsers();
    }

    @SneakyThrows
    @Test
    void deleteWhenInvokeThenNoContentStatus() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokeThenStatusOK() {
        when(userService.updateUser(userRequestDto, userId)).thenReturn(userResponseDto);
        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userResponseDto), result);
        verify(userService).updateUser(userRequestDto, userId);
    }
}