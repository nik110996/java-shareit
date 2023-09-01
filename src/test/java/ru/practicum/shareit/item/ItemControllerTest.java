package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static Long userId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private ItemDtoRequest itemDto;
    private CommentDto commentDto;
    private Long itemId;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDtoRequest.builder()
                .description("desc")
                .available(true).build();
        itemId = 0L;
        commentDto = CommentDto.builder().text("test").build();
    }

    @SneakyThrows
    @Test
    void createItemTest() {
        ItemDtoResponse response = ItemDtoMapper.toItemDtoResponse(itemDto);
        itemDto.setName("Name");
        when(itemService.createItem(itemDto, userId)).thenReturn(response);
        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @SneakyThrows
    @Test
    void createItemBodyNotValidTest() {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItemNotHeadUserIdTest() {
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItemUserNotFoundTest() {
        Long wrongUserId = 100L;
        ResultActions resultActions = mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", wrongUserId.toString())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemDto)));

        resultActions.andExpect(status().isBadRequest());
        String body = resultActions.andReturn().getResponse().getContentAsString();

        verify(itemService, never()).createItem(itemDto, wrongUserId);
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        ItemDtoResponse response = ItemDtoMapper.toItemDtoResponse(itemDto);
        when(itemService.updateItem(itemId, response, userId)).thenReturn(response);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @SneakyThrows
    @Test
    void updateItemTestUserNotFound() {
        userId = 2L;
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));
        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItemByItemIdTest() {
        ItemDtoBC itemBooked = ItemDtoBC.builder()
                .description("desc")
                .available(true).build();
        when(itemService.getItem(itemId, userId)).thenReturn(itemBooked);
        String result = mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemBooked), result);
    }

    @SneakyThrows
    @Test
    void getItemIdStatusNotFound() {
        when(itemService.getItem(itemId, userId))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllItemsTest() {
        Integer from = 1;
        Integer size = 1;
        List<ItemDtoBC> itemBookedList = List.of(ItemDtoBC.builder()
                .description("desc")
                .available(true).build());
        when(itemService.getAllItems(userId, from, size)).thenReturn(itemBookedList);
        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemBookedList), result);
    }

    @SneakyThrows
    @Test
    void getItemBySearchTest() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        List<ItemDtoResponse> itemDtoList = List.of(ItemDtoResponse.builder().build());
        when(itemService.getItemBySearch(text, userId, from, size)).thenReturn(itemDtoList);
        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(commentDto);
        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

    @SneakyThrows
    @Test
    void createCommentWhenItemNotFoundThenStatusNotFound() {
        when(itemService.createComment(userId, itemId, commentDto))
                .thenThrow(new ItemNotFoundException("Предмет не найден"));
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }
}
