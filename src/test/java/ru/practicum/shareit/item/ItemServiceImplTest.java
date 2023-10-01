package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import ru.practicum.shareit.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final UserMapper userMapper = Mappers.getMapper((UserMapper.class));
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    private ItemService itemService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                itemMapper,
                itemRequestRepository,
                userService,
                userMapper,
                bookingRepository,
                bookingMapper,
                commentRepository,
                commentMapper);
    }

    @Test
    void createItem_withoutItemRequest_saveItemAndReturnDto() {
        // arrange
        ItemPostRequestDto requestDto = Generator.makeItemPostRequestDtoWithoutRequestId();
        User owner = Generator.makeUser1();
        when(userService.getUserById(anyLong())).thenReturn(userMapper.toDto(owner));
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    Item item1 = invocationOnMock.getArgument(0, Item.class);
                    item1.setId(1L);
                    return item1;
                });

        // act and assert
        assertThat(itemService.createItem(owner.getId(), requestDto))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", requestDto.getName())
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrPropertyWithValue("available", requestDto.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", requestDto.getRequestId());

        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void createItem_withItemRequestId_saveItemAndReturnDto() {
        // arrange
        ItemPostRequestDto requestDto = Generator.makeItemPostRequestDtoWithoutRequestId();

        ItemRequest itemRequest = Generator.makeItemRequest1();
        requestDto.setRequestId(itemRequest.getId());
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        User owner = Generator.makeUser1();
        when(userService.getUserById(anyLong())).thenReturn(userMapper.toDto(owner));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> {
                    Item item1 = invocationOnMock.getArgument(0, Item.class);
                    item1.setId(1L);
                    return item1;
                });

        // act and assert
        assertThat(itemService.createItem(owner.getId(), requestDto))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", requestDto.getName())
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrPropertyWithValue("available", requestDto.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", requestDto.getRequestId());

        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void patchItem_null_skip() {
        // arrange
        User owner = Generator.makeUser1();
        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));

        ItemPatchRequestDto patch = ItemPatchRequestDto.builder().build();
        final long itemId = item.getId();
        final long ownerId = item.getOwner().getId();
        final long requestId = itemRequest.getId();

        // act and assert
        assertThat(itemService.patchItem(itemId, patch, ownerId))
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("ownerId", ownerId)
                .hasFieldOrPropertyWithValue("requestId", requestId)
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable());

        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    void patchItem_maximumChanges_update() {
        // arrange
        User owner = Generator.makeUser1();
        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));

        ItemPatchRequestDto patch = ItemPatchRequestDto.builder()
                .name("updated name")
                .description("updated description")
                .available(!item.getIsAvailable())
                .build();
        final long itemId = item.getId();
        final long ownerId = item.getOwner().getId();
        final long requestId = itemRequest.getId();

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));

        // act and assert
        assertThat(itemService.patchItem(itemId, patch, ownerId))
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("ownerId", ownerId)
                .hasFieldOrPropertyWithValue("requestId", requestId)
                .hasFieldOrPropertyWithValue("name", patch.getName())
                .hasFieldOrPropertyWithValue("description", patch.getDescription())
                .hasFieldOrPropertyWithValue("available", patch.getAvailable());

        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void patchItem_noChanges_skip() {
        // arrange
        User owner = Generator.makeUser1();
        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));

        ItemPatchRequestDto patch = ItemPatchRequestDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
        final long itemId = item.getId();
        final long ownerId = item.getOwner().getId();
        final long requestId = itemRequest.getId();

        // act and assert
        assertThat(itemService.patchItem(itemId, patch, ownerId))
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("ownerId", ownerId)
                .hasFieldOrPropertyWithValue("requestId", requestId)
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable());

        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    void getItem_ownerIsDifferent_returnDtoWithoutBookingHistory() {
        // arrange
        User owner = Generator.makeUser1();
        long ownerId = owner.getId();

        User other = Generator.makeUser2();
        final long otherId = other.getId();

        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        final long itemId = item.getId();

        Comment comment = Comment.builder()
                .text("new comment")
                .id(1L)
                .author(other)
                .item(item)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));

        // act and assert
        assertThat(itemService.getItem(itemId, otherId))
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("ownerId", ownerId)
                .hasFieldOrPropertyWithValue("requestId", item.getItemRequest().getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("comments", commentMapper.toDtoList(Collections.singletonList(comment)));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void getItem_sameOwner_returnDtoWithBookingHistory() {
        // arrange
        User owner = Generator.makeUser1();
        long ownerId = owner.getId();

        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        final long itemId = item.getId();

        User other = Generator.makeUser2();

        Comment comment = Comment.builder()
                .text("new comment")
                .id(1L)
                .author(other)
                .item(item)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));
        when(bookingRepository.findLastByItemId(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.findFutureByItemId(anyLong())).thenReturn(Optional.empty());

        // act
        ItemAnswerDto result = itemService.getItem(itemId, ownerId);

        // assert
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("ownerId", ownerId)
                .hasFieldOrPropertyWithValue("requestId", item.getItemRequest().getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                .hasFieldOrPropertyWithValue("comments", commentMapper.toDtoList(Collections.singletonList(comment)));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findLastByItemId(anyLong());
        verify(bookingRepository, times(1)).findFutureByItemId(anyLong());
    }

    @Test
    void getItemsByOwnerId_itemsExist_returnNotEmptyDtoList() {
        // arrange
        User owner = Generator.makeUser1();
        long ownerId = owner.getId();

        ItemRequest itemRequest = Generator.makeItemRequest1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        final long itemId = item.getId();

        User other = Generator.makeUser2();

        Comment comment = Comment.builder()
                .text("new comment")
                .id(1L)
                .author(other)
                .item(item)
                .build();

        when(itemRepository.findByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.singletonList(comment));
        when(bookingRepository.findLastByItemId(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.findFutureByItemId(anyLong())).thenReturn(Optional.empty());

        // act
        List<ItemAnswerDto> actual = itemService.getItemsByOwnerId(ownerId);

        // assert
        assertThat(actual)
                .asList()
                .hasSize(1)
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", itemId)
                        .hasFieldOrPropertyWithValue("ownerId", ownerId)
                        .hasFieldOrPropertyWithValue("requestId", item.getItemRequest().getId())
                        .hasFieldOrPropertyWithValue("name", item.getName())
                        .hasFieldOrPropertyWithValue("description", item.getDescription())
                        .hasFieldOrPropertyWithValue("available", item.getIsAvailable())
                        .hasFieldOrPropertyWithValue("comments", commentMapper.toDtoList(Collections.singletonList(comment))));

        verify(itemRepository, times(1)).findByOwnerId(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findLastByItemId(anyLong());
        verify(bookingRepository, times(1)).findFutureByItemId(anyLong());
    }

    @Test
    public void searchItems_WithEmptyText_returnEmptyList() {
        // Arrange
        String searchText = "";

        // Act
        List<ItemAnswerDto> result = itemService.searchItems(searchText);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void searchItems_withText_returnNotEmptyList() {
        // Arrange
        String searchText = "item";
        User owner = Generator.makeUser1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        List<Item> items = Collections.singletonList(item);

        List<ItemAnswerDto> expected = itemMapper.toDtoList(items);
        when(itemRepository.search(searchText)).thenReturn(items);

        // Act
        List<ItemAnswerDto> actual = itemService.searchItems(searchText);

        // Assert
        assertIterableEquals(expected, actual);
    }

    @Test
    public void createComment_validate_saveAndReturnDto() {
        // Arrange
        User owner = Generator.makeUser1();

        Item item = Generator.makeItemWithoutOwner();
        item.setOwner(owner);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        User author = Generator.makeUser2();
        Booking lastBooking = Booking.builder()
                .booker(author)
                .startDate(OffsetDateTime.now().minusDays(3))
                .endDate(OffsetDateTime.now().minusDays(2))
                .status(Status.APPROVED)
                .item(item)
                .id(1L)
                .build();

        when(userService.getUserById(author.getId())).thenReturn(userMapper.toDto(author));

        when(bookingRepository.findLastBookedByBookerIdAndItemId(2L, 1L))
                .thenReturn(Optional.of(lastBooking));


        Comment comment = Comment.builder()
                .text("comment")
                .item(item)
                .author(author)
                .build();

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            Comment arg = invocationOnMock.getArgument(0, Comment.class);
            arg.setId(1L);
            arg.setCreated(OffsetDateTime.now());
            return arg;
        });

        // Act
        CommentPostRequestDto commentPostRequestDto = new CommentPostRequestDto(comment.getText());
        CommentAnswerDto actual = itemService.createComment(author.getId(), commentPostRequestDto, item.getId());

        // Assert
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorName", author.getName());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void findById_notFound_throwException() {
        // Arrange
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and assert
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void checkLastBookedByBookerIdAndItemId_notFound_throwException() {
        // Arrange
        CommentPostRequestDto commentDto = new CommentPostRequestDto("new comment");
        long authorId = 1L;
        long itemId = 1L;

        // Act and assert
        assertThrows(NotAvailableException.class, () -> itemService.createComment(authorId, commentDto, itemId));
    }

    @Test
    void findByIdAndOwnerId_notFound_throwException() {
        // Arrange
        ItemPatchRequestDto patchDto = ItemPatchRequestDto.builder().build();
        long ownerId = 1L;
        long itemId = 1L;

        // Act and assert
        assertThrows(NotFoundException.class, () -> itemService.patchItem(ownerId, patchDto, itemId));
    }

    @Test
    void findItemRequestById_notFound_throwException() {
        // Arrange
        ItemPostRequestDto requestDto = Generator.makeItemPostRequestDtoWithoutRequestId();
        requestDto.setRequestId(1L);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        long ownerId = 1L;

        // Act and assert
        assertThrows(NotFoundException.class, () -> itemService.createItem(ownerId, requestDto));
    }
}