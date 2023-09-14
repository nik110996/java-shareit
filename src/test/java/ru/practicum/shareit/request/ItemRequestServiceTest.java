package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.implementation.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.interfaces.UserService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager entityManager;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;
    private Long userId;
    private ItemRequestDtoRequest itemRequestDto;
    private List<ItemRequestDtoRequest> sourceRequests;
    private UserRequestDto userDtoNew;

    @BeforeEach
    void beforeEach() {
        UserRequestDto userDto = UserRequestDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.createUser(userDto).getId();
        itemRequestDto = ItemRequestDtoRequest.builder().description("description").build();
        sourceRequests = new ArrayList<>(List.of(
                ItemRequestDtoRequest.builder().description("desc1").build(),
                ItemRequestDtoRequest.builder().description("desc2").build(),
                ItemRequestDtoRequest.builder().description("desc3").build()));
        userDtoNew = UserRequestDto.builder()
                .name("NewUser")
                .email("newuser@email.ru").build();
    }

    @SneakyThrows
    @Test
    void create() {
        Long itemRequestId = itemRequestService.createRequest(itemRequestDto, userId).getId();
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequestSaved = query.setParameter("id", itemRequestId).getSingleResult();
        assertNotNull(itemRequestSaved.getId());
        assertEquals(itemRequestSaved.getDescription(), itemRequestDto.getDescription());
        assertNotNull(itemRequestSaved.getRequester());
        assertNotNull(itemRequestSaved.getCreated());
    }

    @Test
    void getAllRequestByUser() {
        userId = userService.createUser(userDtoNew).getId();
        for (ItemRequestDtoRequest requestDto : sourceRequests) {
            itemRequestService.createRequest(requestDto, userId);
        }
        List<ItemRequestDtoResponse> targetRequests = itemRequestService.getRequests(userId);
        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDtoRequest sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getRequestById() {
        Long itemRequestId = itemRequestService.createRequest(itemRequestDto, userId).getId();
        ItemRequestDtoResponse targetItemRequest = itemRequestService.findRequestById(userId, itemRequestId);
        assertNotNull(targetItemRequest.getId());
        assertEquals(targetItemRequest.getDescription(), itemRequestDto.getDescription());
        assertNotNull(targetItemRequest.getCreated());
    }

    @Test
    void getAllRequests() {
        Long userId2 = userService.createUser(userDtoNew).getId();
        for (ItemRequestDtoRequest requestDto : sourceRequests) {
            itemRequestService.createRequest(requestDto, userId2);
        }
        List<ItemRequestDtoResponse> targetRequests = itemRequestService.getRequests(userId, 0, 5);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDtoRequest sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getAllRequestsWithPaging() {
        Long userId2 = userService.createUser(userDtoNew).getId();
        for (ItemRequestDtoRequest requestDto : sourceRequests) {
            itemRequestService.createRequest(requestDto, userId2);
        }
        int from = 0;
        int size = 2;
        List<ItemRequestDtoResponse> targetRequests = itemRequestService.getRequests(userId, from, size);
        assertThat(targetRequests, hasSize(size));
        from = 2;
        targetRequests = itemRequestService.getRequests(userId2, from, size);
        assertThat(targetRequests, hasSize(0));
    }
}
