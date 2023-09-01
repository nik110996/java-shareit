package ru.practicum.shareit.booking.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.Pagination;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.interfaces.UserService;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component("bookingServiceImpl")
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDtoMapper bookingDtoMapper;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest booking) {
        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException("Даты начала и конца бронирования не корректны");
        }
        System.out.println(booking.getItemId());
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь не может забронировать свой предмет");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Booking newBooking = bookingDtoMapper.toBooking(booking, item, user, Status.WAITING);
        return bookingDtoMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Override
    @Transactional
    public BookingDtoResponse updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь не является владельцем предмета");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingDtoMapper.toBookingDto(booking);
    }

    @Override
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
        userService.getUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("Пользователь не является владельцем или держателем брони предмета");
        }
        return bookingDtoMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoResponse> getBookingByState(Long userId, String state, Integer from, Integer size) {
        userService.getUser(userId);
        State bookingState;
        List<Booking> bookings;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        Pagination page = new Pagination(from, size);
        switch (bookingState) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                        page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                        page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
                        page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                        page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
        }
        return bookings.stream().map(bookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllBookings(Long userId, String state, Integer from, Integer size) {
        userService.getUser(userId);
        State bookingState;
        List<Booking> bookings;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        Pagination page = new Pagination(from, size);
        if (bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page).isEmpty()) {
            throw new ValidationException("Пользователь не бронировал предмет");
        }
        switch (bookingState) {
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
                        page);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                        page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                        page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                        page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page
                );
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page);
                break;
        }
        return bookings.stream().map(bookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }
}
