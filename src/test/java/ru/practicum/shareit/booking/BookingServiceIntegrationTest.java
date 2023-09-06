package ru.practicum.shareit.booking;



import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private Long userId;
    private Long itemId;
    private BookingDtoRequest bookingRequestDto;
    private UserRequestDto userDtoBooker;
    private final List<User> users = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private Long bookerId;
    private Long ownerId;

    @BeforeEach
    void beforeEach() {
        UserRequestDto userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();

        ItemDtoRequest itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        itemId = itemService.createItem(itemDto, userId).getId();

        bookingRequestDto = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1L)).build();
        userDtoBooker = UserRequestDto.builder()
                .name("Booker")
                .email("booker@mail.ru").build();

        fillUserRepository(users);
        bookerId = users.get(1).getId();
        ownerId = users.get(0).getId();
        fillItemRepository(users, items);
        fillBookingRepository(users, items, bookings);
    }

    @Test
    void create() {
        Long bookerId = userService.createUser(userDtoBooker).getId();
        Long bookingId = bookingService.createBooking(bookerId, bookingRequestDto).getId();
        TypedQuery<Booking> query = entityManager.createQuery(
                "Select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingSaved = query.setParameter("id", bookingId).getSingleResult();
        assertNotNull(bookingSaved);
        assertNotNull(bookingSaved.getItem());
        assertEquals(itemId, bookingSaved.getItem().getId());
        assertNotNull(bookingSaved.getBooker());
        assertEquals(bookerId, bookingSaved.getBooker().getId());
        assertEquals(Status.WAITING, bookingSaved.getStatus());
    }

    @Test
    void getAllBookingsByStateCURRENTWhenInvokeBookerThenReturnListBookingDtoResponse() {
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "CURRENT",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(0).getId()));
    }

    @Test
    void getAllBookingsByStateFUTUREWhenInvokeBookerThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "FUTURE",
                0,
                10);
        assertThat(resultBookings, hasSize(3));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
        assertThat(resultBookings.get(1).getId(), equalTo(bookings.get(3).getId()));
        assertThat(resultBookings.get(2).getId(), equalTo(bookings.get(2).getId()));
    }

    @Test
    void getAllBookingsByStatePASTWhenInvokeBookerThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "PAST",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(1).getId()));
    }

    @Test
    void getAllBookingsByStateWAITINGWhenInvokeBookerThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "WAITING",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
    }

    @Test
    void getAllBookingsByStateREJECTEDWhenInvokeBookerThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getBookingByState(bookerId,
                "REJECTED",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(3).getId()));
    }

    @Test
    void findAllByStateAndStateALLWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings =
                bookingService.getBookingByState(ownerId,
                        "ALL",
                        0,
                        10);
        assertThat(resultBookings, hasSize(1));
    }

    @Test
    void findAllByItemOwnerAndStateALLWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings =
                bookingService.getAllBookings(ownerId,
                        "ALL",
                        0,
                        10);
        assertThat(resultBookings, hasSize(5));
    }

    @Test
    void findAllByItemOwnerAndStateCURRENTWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getAllBookings(ownerId,
                "CURRENT",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(0).getId()));
    }

    @Test
    void findAllByItemOwnerAndStateFUTUREWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getAllBookings(ownerId,
                "FUTURE",
                0,
                10);
        assertThat(resultBookings, hasSize(3));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
        assertThat(resultBookings.get(1).getId(), equalTo(bookings.get(3).getId()));
        assertThat(resultBookings.get(2).getId(), equalTo(bookings.get(2).getId()));
    }

    @Test
    void findAllByItemOwnerAndStatePASTWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getAllBookings(ownerId,
                "PAST",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(1).getId()));
    }

    @Test
    void findAllByItemOwnerAndStateWAITINGWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getAllBookings(ownerId,
                "WAITING",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(4).getId()));
    }

    @Test
    void findAllByItemOwnerAndStateREJECTEDWhenInvokeThenReturnListBookingDto() {
        List<BookingDtoResponse> resultBookings = bookingService.getAllBookings(ownerId,
                "REJECTED",
                0,
                10);
        assertThat(resultBookings, hasSize(1));
        assertThat(resultBookings.get(0).getId(), equalTo(bookings.get(3).getId()));
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
}

