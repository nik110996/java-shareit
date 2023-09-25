package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoBC;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.implementation.ItemServiceDBImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingDtoMapper bookingMapper;

    @InjectMocks
    private ItemServiceDBImpl itemService;

    @Captor
    private ArgumentCaptor<Item> argumentCaptorItem;
    @Captor
    private ArgumentCaptor<Comment> argumentCaptorComment;

    private Long itemId;
    private Long userId;
    private UserResponseDto userDto;
    private User user;
    private ItemDtoResponse itemDto;
    private Item item;
    private ItemDtoBC itemDtoWithBookingsAndComments;
    private BookingDtoForItem booking;
    private List<Comment> comments;
    private Integer from;
    private Integer size;

    @BeforeEach
    void beforeEach() {
        itemId = 0L;
        userId = 0L;
        userDto = UserResponseDto.builder().build();
        user = User.builder().id(0L).build();
        itemDto = ItemDtoResponse.builder().requestId(0L).build();
        item = Item.builder().build();
        from = 1;
        size = 1;
        itemDtoWithBookingsAndComments = ItemDtoBC.builder().build();
        comments = List.of(Comment.builder().build());
        booking = BookingDtoForItem.builder().id(1L).build();
    }

    @Test
    void updateWhenItemNotExistNotFoundException() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(userId, itemDto, itemId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateUserNotOwnerItemNotFoundException() {
        Item itemOld = Item.builder()
                .owner(User.builder().id(1L).build())
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOld));
        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(userId, itemDto, itemId));
        verify(itemRepository, never()).save(any(Item.class));
    }
}


