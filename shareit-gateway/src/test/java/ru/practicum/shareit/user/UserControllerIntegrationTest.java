package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;
    private Long userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        userDto = UserDto.builder().build();
    }

    @Test
    void findAllInvokeStatusOK() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());
        verify(userClient).findAll(1, 1);
    }

    @Test
    void findAllNotValidParamFromStatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllNotValidParamSize0StatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllNotValidParamSizeNegativeStatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllWithoutParamsStatusOk() throws Exception {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userClient).findAll(defaultFrom, defaultSize);
    }

    @Test
    void findByIdInvokeThenStatusOk() throws Exception {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userClient).findById(userId);
    }

    @Test
    void findByIdUserNotFoundStatusNotFound() throws Exception {
        when(userClient.findById(any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
        verify(userClient).findById(userId);
    }

    @Test
    void createInvokeStatus2xxSuccessful() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());
        verify(userClient).create(userDto);
    }

    @Test
    void createNotValidUserNameStatusBadRequest() throws Exception {
        userDto = UserDto.builder()
                .email("user@mail.ru").build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).create(userDto);
    }

    @Test
    void createNotValidUserEmailStatusBadRequest() throws Exception {
        userDto = UserDto.builder()
                .name("User")
                .email(" ").build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).create(userDto);
    }

    @Test
    void createEmptyBodyStatusBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).create(userDto);
    }

    @Test
    void deleteInvokeStatus2xx() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful());
        verify(userClient).delete(userId);
    }

    @Test
    void updateInvokeStatus2xxSuccessful() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());
        verify(userClient).update(userId, userDto);
    }

    @Test
    void updateUserNotFoundStatusNotFound() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();
        when(userClient.update(userId, userDto))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
        verify(userClient).update(userId, userDto);
    }

    @Test
    void updateEmptyBodyStatusBadRequest() throws Exception {
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).update(userId, userDto);
    }
}