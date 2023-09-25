package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBC;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.interfaces.UserService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private Long userId;
    private ItemDtoRequest itemDto;
    private UserRequestDto userDto;

    @Test
    @Order(1)
    void getById() {
        em.createNativeQuery("Drop table USER");
        userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();

        itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        Long itemId = itemService.createItem(itemDto, userId).getId();
        LocalDateTime start = LocalDateTime.now();
        UserRequestDto userDtoBooker = UserRequestDto.builder()
                .name("Booker")
                .email("booker@email.ru").build();
        Long bookerId = userService.createUser(userDtoBooker).getId();
        BookingDtoRequest lastBookingDtoInitial = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        Long bookingIdLast = bookingService.createBooking(bookerId, lastBookingDtoInitial).getId();
        BookingDtoRequest nextBookingDtoInitial = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        Long bookingIdNext = bookingService.createBooking(bookerId, nextBookingDtoInitial).getId();
        bookingService.updateBooking(userId, bookingIdLast, true);
        bookingService.updateBooking(userId, bookingIdNext, true);
        ItemDtoBC itemTarget = itemService.getItem(userId, itemId);
        assertNotNull(itemTarget.getId());
        assertEquals(itemTarget.getName(), itemDto.getName());
        assertEquals(itemTarget.getDescription(), itemDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void createItem() {
        UserRequestDto userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        Long userId = userService.createUser(userDto).getId();

        itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        Long itemId = itemService.createItem(itemDto, userId).getId();
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemSaved = query.setParameter("name", itemDto.getName()).getSingleResult();
        assertNotNull(itemSaved.getId());
        assertEquals(itemSaved.getName(), itemDto.getName());
        assertEquals(itemSaved.getDescription(), itemDto.getDescription());
        assertEquals(itemSaved.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemSaved.getOwner());
    }


    @Test
    void findAllItems() {
        userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();

        itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        Long itemId = itemService.createItem(itemDto, userId).getId();
        LocalDateTime start = LocalDateTime.now();
        UserRequestDto userDtoBooker = UserRequestDto.builder()
                .name("Booker")
                .email("booker@email.ru").build();
        Long bookerId = userService.createUser(userDtoBooker).getId();
        BookingDtoRequest lastBookingDtoInitial = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusNanos(10L)).build();
        Long bookingIdLast = bookingService.createBooking(bookerId, lastBookingDtoInitial).getId();
        BookingDtoRequest nextBookingDtoInitial = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(start.plusHours(1L))
                .end(start.plusHours(2L)).build();
        Long bookingIdNext = bookingService.createBooking(bookerId, nextBookingDtoInitial).getId();
        bookingService.updateBooking(userId, bookingIdLast, true);
        bookingService.updateBooking(userId, bookingIdNext, true);
        List<ItemDtoBC> itemsTarget = itemService.getAllItems(userId, 0, 5);
        assertNotNull(itemsTarget);
        assertFalse(itemsTarget.isEmpty());
        ItemDtoBC itemTarget = itemsTarget.get(0);
        assertNotNull(itemTarget.getId());
        assertEquals(itemTarget.getName(), itemDto.getName());
        assertEquals(itemTarget.getDescription(), itemDto.getDescription());
        assertEquals(itemTarget.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemTarget.getLastBooking());
        assertEquals(itemTarget.getLastBooking().getId(), bookingIdLast);
        assertEquals(itemTarget.getNextBooking().getId(), bookingIdNext);
    }

    @Test
    void update() {
        userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();

        itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        ItemDtoRequest itemDto1 = ItemDtoRequest.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true).build();
        itemService.createItem(itemDto1, userId);
        ItemDtoRequest itemDto2 = ItemDtoRequest.builder()
                .name("Пила")
                .description("Простая")
                .available(true).build();
        itemService.createItem(itemDto2, userId);
        List<ItemDtoRequest> itemsExpected = List.of(itemDto1);
        List<ItemDtoResponse> targetItems = itemService.getItemBySearch("дрель", userId, 0, 5);
        assertThat(targetItems, hasSize(itemsExpected.size()));
        for (ItemDtoRequest sourceRequest : itemsExpected) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceRequest.getName())),
                    hasProperty("description", equalTo(sourceRequest.getDescription()))
            )));
        }
    }

    @Test
    void createComment() {
        userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();

        itemDto = ItemDtoRequest.builder()
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true).build();
        Long itemId = itemService.createItem(itemDto, userId).getId();
        UserRequestDto userDtoBooker = UserRequestDto.builder()
                .name("Booker")
                .email("booker@email.ru").build();
        Long bookerId = userService.createUser(userDtoBooker).getId();
        BookingDtoRequest lastBookingDtoInitial = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(100)).build();
        Long bookingIdLast = bookingService.createBooking(bookerId, lastBookingDtoInitial).getId();
        bookingService.updateBooking(userId, bookingIdLast, true);
        CommentDto commentDto = CommentDto.builder().text("text").build();
        itemService.createComment(bookerId, itemId, commentDto);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment commentSaved = query.setParameter("text", commentDto.getText()).getSingleResult();
        assertNotNull(commentSaved.getId());
        assertEquals(commentSaved.getText(), commentDto.getText());
        assertEquals(commentSaved.getAuthor().getName(), userDtoBooker.getName());
        assertNotNull(commentSaved.getItem());
    }
}

