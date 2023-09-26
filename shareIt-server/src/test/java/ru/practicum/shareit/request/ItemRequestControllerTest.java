package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private Long userId;
    private Long requestId;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private ItemRequestDtoRequest itemRequestDtoRequest;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        requestId = 0L;
        itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        itemRequestDtoRequest = ItemRequestDtoRequest.builder().build();

    }

    @SneakyThrows
    @Test
    void createRequestTest() {
        itemRequestDtoResponse.setDescription("desc");
        itemRequestDtoRequest.setDescription("desc");
        when(itemRequestService.createRequest(itemRequestDtoRequest, userId)).thenReturn(itemRequestDtoResponse);
        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void getRequestsByUserTest() {
        List<ItemRequestDtoResponse> itemRequestDtoList = List.of(ItemRequestDtoResponse.builder()
                .description("desc").build());
        when(itemRequestService.getRequests(userId)).thenReturn(itemRequestDtoList);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @SneakyThrows
    @Test
    void findRequestByIdTest() {
        itemRequestDtoRequest.setDescription("desc");
        when(itemRequestService.findRequestById(userId, requestId)).thenReturn(itemRequestDtoResponse);
        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void getAllRequestsTest() {
        List<ItemRequestDtoResponse> itemRequestDtoList = List.of(ItemRequestDtoResponse.builder()
                .description("desc").build());
        when(itemRequestService.getRequests(userId, 1, 1)).thenReturn(itemRequestDtoList);
        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }
}