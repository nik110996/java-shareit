package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.interfaces.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingDto booking) {
        log.info("Пришел запрос / эндпоинт: '{} {} с телом {} и с заголовком {}'",
                "POST", "/bookings", booking, userId);
        BookingDto bookingDto = bookingService.createBooking(userId, booking);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "POST", "/bookings", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Пришел запрос / эндпоинт: '{} {} с параметром {} с заголовком {}'",
                "PATCH", "/bookings/" + bookingId, approved, userId);
        BookingDto updatedBookingDto = bookingService.updateBooking(userId, bookingId, approved);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "PATCH", "/bookings/" + bookingId, updatedBookingDto);
        return updatedBookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Пришел запрос / эндпоинт: '{} {} с заголовком {}'", "GET", "/bookings/" + bookingId, userId);
        BookingDto bookingDto = bookingService.getBooking(userId, bookingId);
        log.info("Получен ответ / эндпоинт: '{} {}' с телом '{}", "GET", "/bookings/" + bookingId, bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        System.out.println(state);
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/bookings", userId, state);
        List<BookingDto> bookingDtoList = bookingService.getBookingByState(userId, state);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/bookings", bookingDtoList);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос / эндпоинт: '{} {} с заголовком {} и с параметром {}'",
                "GET", "/bookings/owner", userId, state);
        List<BookingDto> bookingDtoList = bookingService.getAllBookings(userId, state);
        log.info("Получен ответ / эндпоинт: '{} {} с  телом {}'",
                "GET", "/bookings/owner", bookingDtoList);
        return bookingDtoList;
    }
}
