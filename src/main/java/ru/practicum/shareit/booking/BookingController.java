package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingDtoRequest booking) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'",
                "POST", "/bookings", booking, userId);
        BookingDtoResponse bookingDto = bookingService.createBooking(userId, booking);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/bookings", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Пришел запрос / эндпоинт: '{} {} с параметром {} с заголовком {}'",
                "PATCH", "/bookings/" + bookingId, approved, userId);
        BookingDtoResponse updatedBookingDto = bookingService.updateBooking(userId, bookingId, approved);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "PATCH", "/bookings/" + bookingId, updatedBookingDto);
        return updatedBookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/bookings/" + bookingId, userId);
        BookingDtoResponse bookingDto = bookingService.getBooking(userId, bookingId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/bookings/" + bookingId, bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0", required = false)
                                                      @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10", required = false)
                                                      @Min(1) Integer size) {
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/bookings", userId, state);
        List<BookingDtoResponse> bookingDtoList = bookingService.getBookingByState(userId, state, from, size);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/bookings", bookingDtoList);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0", required = false)
                                                   @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false)
                                                   @Min(1) Integer size) {
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/bookings/owner", userId, state);
        List<BookingDtoResponse> bookingDtoList = bookingService.getAllBookings(userId, state, from, size);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/bookings/owner", bookingDtoList);
        return bookingDtoList;
    }
}
