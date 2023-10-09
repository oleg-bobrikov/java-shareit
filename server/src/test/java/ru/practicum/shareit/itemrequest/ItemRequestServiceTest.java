package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.Generator;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private ItemRequestService itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(
                userRepository,
                itemRequestRepository,
                itemRequestMapper,
                itemRepository,
                itemMapper);
    }

    @Test
    void createRequest_byUserIdAndDescription_returnDto() {
        // Arrange
        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        Long id = 1L;
        User requester = Generator.makeUser1();
        long userId = requester.getId();
        ItemRequestAnswerDto expected = ItemRequestAnswerDto.builder()
                .description(requestDto.getDescription())
                .id(id)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));

        when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequest answer = invocationOnMock.getArgument(0, ItemRequest.class);
                    answer.setId(id);
                    return answer;
                });

        // Act
        ItemRequestAnswerDto actual = itemRequestService.createRequest(userId, requestDto);

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", expected.getId())
                .hasFieldOrPropertyWithValue("description", expected.getDescription());

        verify(itemRequestRepository, times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void getRequestsByOwner_byRequesterId_returnListDto() {
        // Arrange
        User requester = Generator.makeUser1();
        long requesterId = requester.getId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        long requestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDto.getDescription())
                .createdDate(LocalDateTime.now())
                .requester(requester)
                .build();
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDateDesc(anyLong()))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestAnswerDto> expected = Collections.singletonList(itemRequestMapper.toDto(itemRequest));

        // Act
        List<ItemRequestAnswerDto> actual = itemRequestService.getRequestsByOwner(requestId);

        // Assert
        assertThat(actual).asList()
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", expected.get(0).getId())
                        .hasFieldOrPropertyWithValue("description", expected.get(0).getDescription()));

        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedDateDesc(anyLong());
    }

    @Test
    void getAllByOthers_byRequesterIdPageFrom0Size1_returnListDto() {
        // Arrange
        User requester = Generator.makeUser1();
        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        long requestId = 1L;
        int page = 0;
        int size = 1;
        int total = 20;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDto.getDescription())
                .createdDate(LocalDateTime.now())
                .requester(requester)
                .build();
        when(itemRequestRepository.findOthersByRequesterIdOrderByCreatedDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest), PageRequest.of(page, size), total));

        List<ItemRequestAnswerDto> expected = Collections.singletonList(itemRequestMapper.toDto(itemRequest));

        // Act
        List<ItemRequestAnswerDto> actual = itemRequestService.getAllByOthers(requestId, page, size);

        // Assert
        assertThat(actual).asList()
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", expected.get(0).getId())
                        .hasFieldOrPropertyWithValue("description", expected.get(0).getDescription()));

        verify(itemRequestRepository, times(1))
                .findOthersByRequesterIdOrderByCreatedDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByOthers_byRequesterIdPageFrom1Size1_returnListDto() {
        // Arrange
        User requester = Generator.makeUser1();
        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        long requestId = 1L;
        int page = 1;
        int size = 1;
        int total = 20;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDto.getDescription())
                .createdDate(LocalDateTime.now())
                .requester(requester)
                .build();
        when(itemRequestRepository.findOthersByRequesterIdOrderByCreatedDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest), PageRequest.of(page, size), total));

        List<ItemRequestAnswerDto> expected = Collections.singletonList(itemRequestMapper.toDto(itemRequest));

        // Act
        List<ItemRequestAnswerDto> actual = itemRequestService.getAllByOthers(requestId, page, size);

        // Assert
        assertThat(actual).asList()
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", expected.get(0).getId())
                        .hasFieldOrPropertyWithValue("description", expected.get(0).getDescription()));

        verify(itemRequestRepository, times(1))
                .findOthersByRequesterIdOrderByCreatedDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getById_userNotFound_throwNotFoundException() {
        // Arrange
        long id = 1L;
        long userId = 100L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and assert
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(id, userId));
    }

    @Test
    void getById_userFound_returnDto() {
        // Arrange
        User requester = Generator.makeUser1();
        long requesterId = requester.getId();
        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        long requestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDto.getDescription())
                .createdDate(LocalDateTime.now())
                .requester(requester)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestAnswerDto expected = ItemRequestAnswerDto.builder()
                .id(requestId)
                .description(requestDto.getDescription())
                .build();
        // Act
        ItemRequestAnswerDto actual = itemRequestService.getById(requestId, requesterId);

        //Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", expected.getId())
                .hasFieldOrPropertyWithValue("description", expected.getDescription());

        verify(itemRequestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getById_requestNotFound_throwNotFoundException() {
        // Arrange
        User requester = Generator.makeUser1();
        long requesterId = requester.getId();
        long requestId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and assert
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestId, requesterId));
    }
}