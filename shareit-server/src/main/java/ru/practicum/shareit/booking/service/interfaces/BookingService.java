package ru.practicum.shareit.booking.service.interfaces;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(Long userId, BookingDtoRequest booking);

    BookingDtoResponse updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getBookingByState(Long userId, String state, Integer from, Integer size);

    List<BookingDtoResponse> getAllBookings(Long userId, String state, Integer from, Integer size);
}
