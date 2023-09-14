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
import ru.practicum.shareit.item.service.implementation.ItemServiceDBImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.implemintation.UserServiceDBImp;
import java.time.LocalDateTime;
import java.util.List;
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
    @InjectMocks
    ItemServiceDBImpl itemServiceDB;
    private BookingDtoMapper bookingDtoMapper;


    @BeforeEach
    void init() {
        bookingDtoMapper = new BookingDtoMapper();
        bookingService = new BookingServiceImpl(bookingDtoMapper, bookingRepository,
                itemRepository, userRepository, userServiceDBImp);
    }


    @Test
    void createBookingNullPointerTest() throws BookingNotFoundException {
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
    void createBookingValidationExceptionTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(owner.getId(), dto));
    }

    @Test
    void createBookingUserNotFoundExceptionTest() {
        BookingDtoRequest dto = BookingDtoRequest.builder().build();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);
        item.getOwner().setId(owner.getId());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(owner.getId(), dto));
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
        ItemDtoResponse itemDto = new ItemDtoResponse(1L, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setRequest(request);
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
    void updateBookingUserNotFoundTest() throws BookingNotFoundException {
        long itemId = 1L;
        long bookerId = 1L;
        User newUser = new User(1L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoResponse itemDto = new ItemDtoResponse(1L, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setRequest(request);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDtoResponse dto = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.WAITING);
        Booking booking = bookingDtoMapper.toBooking(dto, item, newUser);
        booking.getItem().getOwner().setId(2L);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingService.updateBooking(bookerId, newUser.getId(), true));
    }

    @Test
    void updateBookingValidationExceptionTest() throws BookingNotFoundException {
        long itemId = 1L;
        long bookerId = 1L;
        User newUser = new User(1L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoResponse itemDto = new ItemDtoResponse(1L, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setRequest(request);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDtoResponse dto = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.WAITING);
        Booking booking = bookingDtoMapper.toBooking(dto, item, newUser);
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(bookerId, newUser.getId(), true));
    }

    @Test
    void updateBookingAndRejectedTest() throws BookingNotFoundException {
        long itemId = 1L;
        long bookerId = 1L;
        User newUser = new User(1L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoResponse itemDto = new ItemDtoResponse(1L, "TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setRequest(request);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDtoResponse dto = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.WAITING);
        BookingDtoResponse dto2 = new BookingDtoResponse(1L, itemId, start, end, itemDto, userRequestDto, Status.REJECTED);
        Booking booking = bookingDtoMapper.toBooking(dto, item, newUser);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDtoResponse updated = bookingService.updateBooking(bookerId, newUser.getId(), false);
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
        booking.getBooker().setId(2L);
        booking.getItem().getOwner().setId(2L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
    }

    @Test
    void getBookingNotOwnerTest() throws BookingNotFoundException {
        long bookerId = 1L;
        assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(1L, bookerId));
    }

   /* @Test
    void getAllBookingsByStateCURRENTWhenInvokeBookerThenReturnListBookingDto() {
        User newUser = new User(1L, "test", "test@test.com");
        User owner = new User(2L, "test", "test@test.com");
        UserRequestDto userRequestDto = UserDtoMapper.toUserRequestDto(newUser);
        final ItemRequest request = new ItemRequest(0L, "description", newUser, null, LocalDateTime.now());
        ItemDtoRequest itemDto = new ItemDtoRequest("TestItem", "DescriptionTest", true, request.getId());
        Item item = ItemDtoMapper.toItem(itemDto, newUser);
        item.setId(1L);
        //Booking booking1 = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), newUser, item, Status.WAITING);
        BookingDtoRequest booking1 = new BookingDtoRequest(item.getId(), LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
       // bookingDtoMapper.toBooking(2L, booking1)
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        bookingService.createBooking(2L, booking1);
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "CURRENT",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(0).getId()));
    }*/

    private void fillBookingRepository(List<User> users,
                                       List<Item> items,
                                       List<Booking> bookings) {
        LocalDateTime start = LocalDateTime.now();
        Booking booking2 = Booking.builder()
                .start(start.minusHours(1))
                .end(start.plusHours(1))
                .item(items.get(0))
                .booker(users.get(1))
                .status(Status.APPROVED).build();
        Booking booking3 = Booking.builder()
                .start(start.minusHours(2))
                .end(start.minusHours(1))
                .item(items.get(0))
                .booker(users.get(1))
                .status(Status.APPROVED).build();
        Booking booking4 = Booking.builder()
                .start(start.plusHours(1))
                .end(start.plusHours(2))
                .item(items.get(1))
                .booker(users.get(1))
                .status(Status.APPROVED).build();
        Booking booking5 = Booking.builder()
                .start(start.plusHours(2))
                .end(start.plusHours(3))
                .item(items.get(2))
                .booker(users.get(1))
                .status(Status.REJECTED).build();
        Booking booking6 = Booking.builder()
                .start(start.plusHours(3))
                .end(start.plusHours(4))
                .item(items.get(2))
                .booker(users.get(1))
                .status(Status.WAITING).build();
        Booking booking7 = Booking.builder()
                .start(start.plusHours(1))
                .end(start.plusHours(2))
                .item(items.get(3))
                .booker(users.get(0))
                .status(Status.WAITING).build();
        bookings.addAll(List.of(booking2, booking3, booking4, booking5, booking6, booking7));
        for (Booking booking : bookings) {
            booking = bookingRepository.save(booking);
        }
    }

    private void fillUserRepository(List<User> users) {
        User user2 = User.builder()
                .name("User1")
                .email("user1@email.ru").build();
        User user3 = User.builder()
                .name("User2")
                .email("user2@email.ru").build();
        User user4 = User.builder()
                .name("User3")
                .email("user3@email.ru").build();
        users.addAll(List.of(user2, user3, user4));
        for (User user : users) {
            user = userRepository.save(user);
        }
    }

    private void fillItemRepository(List<User> users, List<Item> items) {
        Item item2 = Item.builder()
                .name("Отвертка")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item3 = Item.builder()
                .name("Дрель")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item4 = Item.builder()
                .name("Пила")
                .description("Description")
                .available(true)
                .owner(users.get(0)).build();
        Item item5 = Item.builder()
                .name("Шуруповерт")
                .description("Description")
                .available(true)
                .owner(users.get(1)).build();
        items.addAll(List.of(item2, item3, item4, item5));
        for (Item item : items) {
            item = itemRepository.saveAndFlush(item);
        }
    }
}





