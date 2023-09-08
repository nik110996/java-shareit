package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static Long userId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @BeforeEach
    void beforeEach() {
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(userId, bookingDtoRequest)).thenReturn(bookingDtoResponse);
        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(result);
        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    void createBookingUserNotFoundTest() throws Exception {
        when(bookingService.createBooking(userId, bookingDtoRequest))
                .thenThrow(new UserNotFoundException("Пользователь не может забронировать свой предмет"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createBookingNotValidBodyTest() {
        bookingDtoRequest.setStart(LocalDateTime.now().minusHours(1L));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(userId, bookingDtoRequest);
    }

    @SneakyThrows
    @Test
    void updateBookingTest() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.updateBooking(userId, bookingId, approved)).thenReturn(bookingDtoResponse);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateBookingNoApprovedTest() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.updateBooking(userId, bookingId, approved))
                .thenThrow(ValidationException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().is5xxServerError());
    }

    @SneakyThrows
    @Test
    void updateBookingNotUserOwnerTest() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.updateBooking(userId, bookingId, approved))
                .thenThrow(ValidationException.class);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
    }

    @SneakyThrows
    @Test
    void getBookingTest() {
        Long bookingId = 0L;
        BookingDtoResponse responseDto = BookingDtoResponse.builder().build();
        when(bookingService.getBooking(userId, bookingId)).thenReturn(responseDto);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(responseDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingDuplicateBookingStatusTest() {
        Long bookingId = 0L;
        when(bookingService.getBooking(userId, bookingId))
                .thenThrow(new ValidationException("exception message"));
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookingsTest() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingDtoResponse> responseDtoList = Collections.emptyList();
        when(bookingService.getAllBookings(userId, state, from, size))
                .thenReturn(responseDtoList);
        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }

    @SneakyThrows
    @Test
    void getBookingByStateTest() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingDtoResponse> responseDtoList = Collections.emptyList();
        when(bookingService.getBookingByState(userId, state, from, size))
                .thenReturn(responseDtoList);
        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }
}
