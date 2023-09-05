package ru.practicum.shareit.item.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.request.Pagination;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component("itemServiceDBImp")
@Service
@RequiredArgsConstructor
public class ItemServiceDBImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingDtoMapper bookingDtoMapper;

    @Override
    @Transactional
    public ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        User user = UserDtoMapper.toUser(userService.getUser(userId));
        Item item = ItemDtoMapper.toItem(itemDtoRequest, user);
        Long requestId = itemDtoRequest.getRequestId();
        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден")
                    ));
        }
        return ItemDtoMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto comment) {
        User author = UserDtoMapper.toUser(userService.getUser(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("поользоватль с id = "
                        + userId + " не арендовал предмет с id = " + itemId));
        Comment newComment = CommentDtoMapper.toComment(comment, item, author);
        newComment.setItem(item);
        newComment.setAuthor(author);
        newComment.setCreated(LocalDateTime.now());
        return CommentDtoMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    @Transactional
    public ItemDtoResponse updateItem(long id, ItemDtoResponse itemDto, long userId) {
        userService.getUser(userId);
        Item item = findItemOrThrowException(id);
        if (item == null || item.getOwner().getId() != userId) {
            throw new ItemNotFoundException("Item with id = " + itemDto.getId() + " not found");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public ItemDtoBC getItem(Long itemId, Long userId) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет с таким id не найден"));
        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentDtoMapper::toCommentDto).collect(Collectors.toList());
        BookingDtoForItem lastBooking = null;
        BookingDtoForItem nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(),
                            Status.APPROVED,
                            LocalDateTime.now())
                    .map(bookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);

            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(),
                            Status.APPROVED,
                            LocalDateTime.now())
                    .map(bookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);
        }
        return ItemDtoMapper.toItemDtoBC(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoResponse> getItemBySearch(String text, Long userId, Integer from, Integer size) {
        userService.getUser(userId);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        Pagination pagination = new Pagination(from, size);
        List<ItemDtoResponse> items = itemRepository.getItemBySearch(text, pagination).stream()
                .map(ItemDtoMapper::toItemDtoResponse)
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            throw new ItemNotFoundException("По вашему запросу ничего не найдено");
        }
        return items;
    }

    @Override
    public List<ItemDtoBC> getAllItems(Long userId, Integer from, Integer size) {
        userService.getUser(userId);
        Pagination page = new Pagination(from, size);
        List<ItemDtoBC> items = itemRepository.getAllItems(userId, page)
                .stream()
                .map(item -> {
                    ItemDtoBC itemDto = ItemDtoMapper.toItemDtoBC(item);
                    BookingDtoForItem lastBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                                    item.getId(), Status.APPROVED, LocalDateTime.now())
                            .map(bookingDtoMapper::toBookingDtoForItem)
                            .orElse(null);
                    itemDto.setLastBooking(lastBookingDto);
                    BookingDtoForItem nextBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                    item.getId(), Status.APPROVED, LocalDateTime.now())
                            .map(bookingDtoMapper::toBookingDtoForItem)
                            .orElse(null);
                    itemDto.setNextBooking(nextBookingDto);

                    List<CommentDto> comments = commentRepository.findByItem(item).stream()
                            .map(CommentDtoMapper::toCommentDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(comments);
                    return itemDto;
                })
                .sorted(Comparator.comparingLong(ItemDtoBC::getId))
                .collect(Collectors.toList());
        return items;
    }

    private Item findItemOrThrowException(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item с таким id - не найден"));
    }
}
