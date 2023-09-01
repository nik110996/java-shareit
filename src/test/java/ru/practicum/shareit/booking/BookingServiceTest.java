package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.implementation.BookingServiceImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.implemintation.UserServiceDBImp;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;
    @InjectMocks
    UserServiceDBImp userServiceDBImp;
    private BookingDtoMapper bookingDtoMapper;

    @BeforeEach
    void init() {
        bookingDtoMapper = new BookingDtoMapper();
        bookingService = new BookingServiceImpl(bookingDtoMapper,bookingRepository, itemRepository, userRepository, userServiceDBImp);
    }

    @Test
    void createBooking() throws BookingNotFoundException {
        User newUser = new User(1L, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0L, "description", new User(3L, "test", "test@test.com"), null, LocalDateTime.now());
        ItemDtoRequest itemDto = new ItemDtoRequest("TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, new User(2L, "test2", "test2@test.com"));
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        assertThrows(NullPointerException.class, () -> bookingService.createBooking(1L, dto));
    }

    @Test
    void createBookingItemUnavailableTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        Long bookerId = 2L;
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setAvailable(false);
        item.setDescription("A description");
        item.setOwner(new User(bookerId, "test", "test@test.com"));
        itemRepository.save(item);
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(bookerId, dto));
    }

    @Test
    void createBookingItemIsNotAvailableTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        itemRepository.save(item);
        item.setAvailable(false);
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(owner.getId(), dto));
    }

    @Test
    void createBookingEndDateIsBeforeStartDateTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        itemRepository.save(item);
        item.setAvailable(true);
        dto.setEnd(LocalDateTime.now().plusDays(-1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(owner.getId(), dto));
    }

    @Test
    void createBookingBookerIdIsInvalidTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        itemRepository.save(item);
        assertThrows(ItemNotFoundException.class, () -> {
            bookingService.createBooking(owner.getId(), dto);
            assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(owner.getId(), dto));
        });
    }

    @Test
    void updateBookingNotFoundBookingTest() throws BookingNotFoundException {
        long itemId = 1L;
        long bookerId = 1L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking(bookerId, itemId, true));
    }



    @Test
    void updateBookingTest() throws BookingNotFoundException {
        long itemId = 1L;
        long bookerId = 1L;
        User newUser = new User(1L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoResponse itemDto = new ItemDtoResponse(1L,"TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setItemRequest(request);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDtoResponse dto = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.WAITING);
        BookingDtoResponse dto2 = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.APPROVED);
        Booking booking = bookingDtoMapper.toBooking(dto, item, newUser);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDtoResponse updated = bookingService.updateBooking(bookerId, newUser.getId(), true);
        assertEquals(dto2, updated);
    }

    @Test
    void getBookingTest() throws BookingNotFoundException {
        User newUser = new User(1L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoRequest itemDto = new ItemDtoRequest("TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        ItemDtoResponse itemDtoResponse = ItemDtoMapper.toItemDtoResponse(item);
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .id(1L)
                .booker(userRequestDto)
                .item(itemDtoResponse)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        Booking booking = bookingDtoMapper.toBooking(dto, item, newUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertEquals(dto, bookingService.getBooking(booking.getId(), 1L));
    }

    @Test
    void getBookingNotFoundTest() throws BookingNotFoundException {
        long bookerId = 1L;
        assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(1L, bookerId));
    }
}
