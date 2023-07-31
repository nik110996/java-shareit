package ru.practicum.shareit.item.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.transaction.Transactional;
import java.util.List;

@Primary
@Component("userServiceDBImp")
@Service
@RequiredArgsConstructor
public class ItemServiceDBImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto createItem(Item item, Long userId) {
        User user = UserDtoMapper.toUser(userService.getUser(userId));
        item.setOwner(user);
        return ItemDtoMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, ItemDto itemDto, Long userId) {
        checkUserExisting(UserDtoMapper.toUser(userService.getUser(userId)));
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
    public ItemDto getItem(Long itemId, Long userId) {
        return ItemDtoMapper.toItemDto(findItemOrThrowException(itemId));
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, Long userId) {
        return null;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findAll();
        return ItemDtoMapper.toItemDto(items);
    }

    private Item findItemOrThrowException(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item с таким id - не найден"));
    }

    private void checkUserExisting(User user) {
        if (user == null) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }
}
