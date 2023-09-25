package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;
    private Long itemId;
    private Long userId;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .name("Name")
                .description("desc")
                .available(true).build();
        userId = 0L;
        itemId = 0L;
    }

    @Test
    void createStatus2xxSuccessful() throws Exception {
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is2xxSuccessful());
        verify(itemClient).create(userId, itemDto);
    }

    @Test
    void createNotHeadUserIdStatusBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void createNotBodyStatusBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createNotValidItemNameStatusBadRequest(String input) throws Exception {
        itemDto.setName(input);
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createNotValidItemDescStatusBadRequest(String input) throws Exception {
        itemDto.setDescription(input);
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void createNotValidItemAvailableStatusBadRequest() throws Exception {
        itemDto.setAvailable(null);
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, -15, Long.MIN_VALUE})
    void createNotValidItemRequestIdStatusBadRequest(Long number) throws Exception {
        itemDto.setRequestId(number);
        mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void updateInvokeStatusOk() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
        verify(itemClient).update(userId, itemId, itemDto);
    }

    @Test
    void updateNotHeadUserIdStatusBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).update(any(), any(), any());
    }

    @Test
    void updateEmptyBodyStatusBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).update(any(), any(), any());
    }

    @Test
    void updateResponseStatusNotFoundStatusNotFound() throws Exception {
        when(itemClient.update(any(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdInvokeStatusOk() throws Exception {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());
        verify(itemClient).findById(userId, itemId);
    }

    @Test
    void findByIdResponseStatusNotFoundStatusNotFound() throws Exception {
        when(itemClient.findById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdNotHeadUserIdStatusBadRequest() throws Exception {
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @Test
    void getAllItemsByUserIdInvokeStatusOk() throws Exception {
        Integer from = 1;
        Integer size = 1;
        mockMvc.perform(get("/items")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk());
        verify(itemClient).findAll(userId, from, size);
    }

    @Test
    void getAllItemsByUserIdNotHeadUserIdStatusBadRequest() throws Exception {
        int from = 1;
        int size = 1;

        mockMvc.perform(get("/items")
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findAll(anyLong(), any(), any());
    }

    @Test
    void getAllItemsByUserIdResponseStatusNotFoundStatusNotFound() throws Exception {
        int from = 1;
        int size = 1;
        when(itemClient.findAll(anyLong(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/items")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void getAllItemsByUserIdNotValidParamFromStatusBadRequest(Integer from) throws Exception {
        int size = 1;
        mockMvc.perform(get("/items")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", from.toString())
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void getAllItemsByUserIdNotValidParamSizeStatusBadRequest(Integer size) throws Exception {
        int from = 1;
        mockMvc.perform(get("/items")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", Integer.toString(from))
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @Test
    void deleteInvokeStatusOk() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());
        verify(itemClient, times(1)).delete(userId, itemId);

    }

    @Test
    void deleteResponseStatusNotFoundStatusNotFound() throws Exception {
        when(itemClient.delete(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchInvokeStatusOk() throws Exception {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk());
        verify(itemClient).search(userId, text, from, size);
    }

    @Test
    void searchNotHeadUserIdStatusBadRequest() throws Exception {
        String text = "text";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).search(anyLong(), any(), any(), any());
    }

    @Test
    void searchResponseStatusNotFoundStatusNotFound() throws Exception {
        String text = "text";
        int from = 1;
        int size = 1;
        when(itemClient.search(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("text", text)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void searchNotValidParamFromStatusBadRequest(Integer from) throws Exception {
        String text = "text";
        int size = 1;
        mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void searchNotValidParamSizeStatusBadRequest(Integer size) throws Exception {
        String text = "text";
        int from = 1;
        mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("text", text)
                        .param("from", Integer.toString(from))
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @Test
    void searchNotParamTextStatusBadRequest() throws Exception {
        int size = 1;
        int from = 1;
        mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).findById(anyLong(), anyLong());
    }

    @Test
    void createCommentInvokeStatusCreateDtoInBody() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("test").build();
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().is2xxSuccessful());
        verify(itemClient).createComment(userId, itemId, commentDto);
    }

    @Test
    void createCommentNotHeadUserIdStatusBadRequest() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("test").build();
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).createComment(any(), any(), any());
    }

    @Test
    void createCommentNotBodyStatusBadRequest() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).createComment(any(), any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createCommentNotValidCommentTextStatusBadRequest(String input) throws Exception {
        CommentDto commentDto = CommentDto.builder().text(input).build();
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(any(), any());
    }

    @Test
    void createCommentResponseStatusNotFoundStatusNotFound() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("input").build();
        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }
}