package ru.practicum.shareit.request.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.Pagination;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDtoResponse createRequest(ItemRequestDtoRequest requestDto, Long userId) {
        User user = UserDtoMapper.toUser(userService.getUser(userId));
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(requestDto, user);
        return ItemRequestDtoMapper.toItemRequestDtoResponse(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    @Override
    public List<ItemRequestDtoResponse> getRequests(Long userId) {
        userService.getUser(userId);
        return itemRequestRepository.findAllByRequesterId(
                        userId,
                        Sort.by(Sort.Direction.ASC, "created")).stream()
                .map(ItemRequestDtoMapper::toItemRequestDtoResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<ItemRequestDtoResponse> getRequests(Long userId, Integer from, Integer size) {
        userService.getUser(userId);
        return itemRequestRepository.findAllByRequesterIdIsNot(userId,
                        new Pagination(from, size))
                .stream()
                .map(ItemRequestDtoMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemRequestDtoResponse findRequestById(Long userId, Long requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Такой запрос не найден"));
        return ItemRequestDtoMapper.toItemRequestDtoResponse(itemRequest);
    }
}
