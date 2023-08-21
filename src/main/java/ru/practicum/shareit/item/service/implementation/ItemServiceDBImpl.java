package ru.practicum.shareit.item.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
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

    @Override
    public ItemDto createItem(Item item, Long userId) {
        User user = UserDtoMapper.toUser(userService.getUser(userId));
        item.setOwner(user);
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
    public ItemDto updateItem(long id, ItemDto itemDto, long userId) {
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
                    .map(BookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);

            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(),
                            Status.APPROVED,
                            LocalDateTime.now())
                    .map(BookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);
        }
        return ItemDtoMapper.toItemDtoBC(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, Long userId) {
        userService.getUser(userId);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getItemBySearch(text);
        if (items.isEmpty()) {
            throw new ItemNotFoundException("По вашему запросу ничего не найдено");
        }
        return items.stream().map(ItemDtoMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDtoBC> getAllItems(Long userId) {
        userService.getUser(userId);
        List<Item> items = itemRepository.getAllItems(userId);
        if (items.isEmpty()) {
            throw new ItemNotFoundException("По вашему запросу ничего не найдено");
        }
        return items.stream().map(item -> {
            BookingDtoForItem lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(), Status.APPROVED, LocalDateTime.now())
                    .map(BookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);
            BookingDtoForItem nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), Status.APPROVED, LocalDateTime.now())
                    .map(BookingDtoMapper::toBookingDtoForItem)
                    .orElse(null);
            List<CommentDto> comments = commentRepository.findByItem(item).stream().map(CommentDtoMapper::toCommentDto)
                    .collect(Collectors.toList());
            return ItemDtoMapper.toItemDtoBC(item, lastBooking, nextBooking, comments);
        }).collect(Collectors.toList());
    }

    private Item findItemOrThrowException(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item с таким id - не найден"));
    }
}
