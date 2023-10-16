package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;
    private Long userId;
    private BookingDtoInitial bookingDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime time = LocalDateTime.now();
        bookingDto = BookingDtoInitial.builder()
                .itemId(1L)
                .start(time.plusMinutes(1L))
                .end(time.plusMinutes(5L))
                .build();
        userId = 0L;
    }

    @Test
    void findAllByStateAllStatusOk() throws Exception {
        String state = "ALL";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isOk());
        verify(bookingClient).findAllByState(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByStateNotValidBadRequest() throws Exception {
        String state = "NOtValid";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByState(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByStateAllBadRequest() throws Exception {
        String state = "All";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByState(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByStateAllNotFound() throws Exception {
        String state = "ALL";
        int from = 1;
        int size = 1;
        when(bookingClient.findAllByState(anyLong(), any(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void findAllByStateAllParamFromBadRequest(Integer from) throws Exception {
        String state = "ALL";
        int size = 1;
        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByState(anyLong(), any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void findAllByStateAllParamSizeBadRequest(Integer size) throws Exception {
        String state = "ALL";
        int from = 1;
        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByState(anyLong(), any(), any(), any());
    }

    @Test
    void createInvokeStatus2xxSuccessful() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().is2xxSuccessful());
        verify(bookingClient).create(anyLong(), any());
    }

    @Test
    void createNotHeaderUserIdStatusBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createNotBodyStatusBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, -15, Long.MIN_VALUE})
    @SneakyThrows
    void createNotBodyNotValidItemIdStatusBadRequest(Long itemId) {
        bookingDto.setItemId(itemId);
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createNotBodyNotValidStartStatusBadRequest() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusHours(24L));
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createNotBodyNotStartStatusBadRequest() throws Exception {
        bookingDto.setStart(null);
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createNotBodyNotValidEndStatusBadRequest() throws Exception {
        bookingDto.setEnd(LocalDateTime.now().minusHours(24L));
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createNotBodyNotEndStatusBadRequest() throws Exception {
        bookingDto.setEnd(null);
        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void getBookingByIdInvokeStatusOk() throws Exception {
        Long bookingId = 0L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());
        verify(bookingClient).getBookingById(userId, bookingId);
    }

    @Test
    void getBookingByIdResponseStatusNotFoundStatusNotFound() throws Exception {
        Long bookingId = 0L;
        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingByIdNotHeaderUserIdStatusNotFound() throws Exception {
        Long bookingId = 0L;
        when(bookingClient.getBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByItemOwnerInvokeStatusOk() throws Exception {
        String state = "ALL";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isOk());
        verify(bookingClient).findAllByItemOwner(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByItemOwnerStateNotValidStatusBadRequest() throws Exception {
        String state = "NOtValid";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByItemOwner(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByItemOwnerNotHeaderUserIdStatusBadRequest() throws Exception {
        String state = "All";
        int from = 1;
        int size = 1;
        mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByItemOwner(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByItemOwnerWhenResponseStatusNotFoundThenStatusNotFound() throws Exception {
        String state = "ALL";
        int from = 1;
        int size = 1;
        when(bookingClient.findAllByItemOwner(anyLong(), any(), any(), any()))
                .thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -15, Integer.MIN_VALUE})
    void findAllByItemOwnerNotValidParamFromStatusBadRequest(Integer from) throws Exception {
        String state = "ALL";
        int size = 1;
        mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByItemOwner(anyLong(), any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -15, Integer.MIN_VALUE})
    void findAllByItemOwnerNotValidParamSizeStatusBadRequest(Integer size) throws Exception {
        String state = "ALL";
        int from = 1;
        mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).findAllByItemOwner(anyLong(), any(), any(), any());
    }

    @Test
    void setStatusInvokeStatusOk() throws Exception {
        Long bookingId = 1L;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString())
                        .param("approved", Boolean.toString(approved)))
                .andExpect(status().isOk());
    }

    @Test
    void setStatusNotHeaderUserIdStatusBadRequest() throws Exception {
        Long bookingId = 1L;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", Boolean.toString(approved)))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).setStatus(anyLong(), anyLong(), any());
    }

    @Test
    void setStatusNotApprovedStatusBadRequest() throws Exception {
        Long bookingId = 1L;
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).setStatus(anyLong(), anyLong(), any());
    }
}
