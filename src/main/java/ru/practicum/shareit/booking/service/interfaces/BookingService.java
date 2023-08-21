package ru.practicum.shareit.booking.service.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDtoRequest booking);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingByState(Long userId, String state);

    List<BookingDto> getAllBookings(Long userId, String state);
}
