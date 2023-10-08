package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemAnswerDto createItem(long ownerId, ItemRequestDto requestDto) {

        User owner = userMapper.toModel(userService.getUserById(ownerId));
        Long requestId = requestDto.getRequestId();

        Item item = itemMapper.toModel(requestDto);
        item.setOwner(owner);

        if (requestId != null) {
            item.setItemRequest(findItemRequestById(requestId));
        }

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemAnswerDto updateItem(long ownerId, ItemRequestDto itemDto, long itemId) {
        Item item = findByIdAndOwnerId(itemId, ownerId);

        boolean hasChanged = false;

        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
            hasChanged = true;
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {

            item.setDescription(itemDto.getDescription());
            hasChanged = true;
        }

        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(item.getIsAvailable())) {

            item.setIsAvailable(itemDto.getAvailable());
            hasChanged = true;
        }

        return itemMapper.toDto(hasChanged ? itemRepository.save(item) : item);
    }

    @Override
    public ItemAnswerDto getItem(long itemId, long userId) {
        Item item = findById(itemId);

        ItemAnswerDto itemAnswerDto = itemMapper.toDto(item);

        // add booking history
        if (item.getOwner().getId().equals(userId)) {

            Booking lastBooking = bookingRepository.findLastByItemId(item.getId()).orElse(null);
            itemAnswerDto.setLastBooking(bookingMapper.toShortDto(lastBooking));

            Booking nextBooking = bookingRepository.findFutureByItemId(item.getId()).orElse(null);
            itemAnswerDto.setNextBooking(bookingMapper.toShortDto(nextBooking));
        }

        // add comments
        itemAnswerDto.setComments(commentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));

        return itemAnswerDto;
    }

    @Override
    public List<ItemAnswerDto> getItemsByOwnerId(Long ownerId) {

        return itemRepository.findByOwnerId(ownerId).stream().map(item -> {

            // add booking history
            ItemAnswerDto itemAnswerDto = itemMapper.toDto(item);
            Booking lastBooking = bookingRepository.findLastByItemId(item.getId()).orElse(null);
            itemAnswerDto.setLastBooking(bookingMapper.toShortDto(lastBooking));

            Booking nextBooking = bookingRepository.findFutureByItemId(item.getId()).orElse(null);
            itemAnswerDto.setNextBooking(bookingMapper.toShortDto(nextBooking));

            // add comments
            itemAnswerDto.setComments(commentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));

            return itemAnswerDto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemAnswerDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemMapper.toDtoList(itemRepository.search(text));
    }

    @Override
    public CommentAnswerDto createComment(long authorId, CommentRequestDto commentDto, long itemId) {
        checkLastBookedByBookerIdAndItemId(authorId, itemId);

        Item item = findById(itemId);

        User author = userMapper.toModel(userService.getUserById(authorId));

        Comment comment = Comment.builder()
                .item(item)
                .author(author)
                .text(commentDto.getText())
                .build();
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private Item findById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(
                        String.format("item_Id %s is not found", itemId)));
    }

    private void checkLastBookedByBookerIdAndItemId(long authorId, long itemId) {
        bookingRepository.findLastBookedByBookerIdAndItemId(authorId, itemId)
                .orElseThrow(() -> new NotAvailableException(
                        String.format("item_id %s should be booked by user_id %s", itemId, authorId)));
    }

    private Item findByIdAndOwnerId(long itemId, long ownerId) {
        return itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not exist"));
    }

    private ItemRequest findItemRequestById(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request with id " + id + " is not found"));
    }
}
