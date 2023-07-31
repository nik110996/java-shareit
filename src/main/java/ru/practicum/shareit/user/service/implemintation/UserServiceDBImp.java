package ru.practicum.shareit.user.service.implemintation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.interfaces.UserService;
import javax.transaction.Transactional;
import java.util.List;

@Primary
@Component("userServiceDBImp")
@Service
@RequiredArgsConstructor
public class UserServiceDBImp implements UserService {

    private final UserRepository repository;

    @Override
    public UserResponseDto getUser(Long id) {
        return UserDtoMapper.toUserResponseDto(findUserOrThrowException(id));
    }

    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = repository.findAll();
        return UserDtoMapper.toUserResponseDto(users);
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userDto) {
        User user = UserDtoMapper.toUser(userDto);
        return UserDtoMapper.toUserResponseDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserRequestDto userDto, Long id) {
        User user = findUserOrThrowException(id);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userDto.getId() + " not found");
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return UserDtoMapper.toUserResponseDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteUsers() {
        repository.deleteAll();
    }

    private User findUserOrThrowException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User с таким id - не найден"));
    }
}
