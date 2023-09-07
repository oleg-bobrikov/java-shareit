package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
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
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemAnswerDto createItem(ItemPostRequestDto itemPostRequestDto) {

        User user = userMapper.toModel(userService.getUserById(itemPostRequestDto.getOwnerId()));
        Item item = itemMapper.toModel(itemPostRequestDto);
        item.setOwner(user);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemAnswerDto patchItem(ItemPatchRequestDto itemDto) {
        Item item = itemRepository.findByIdAndOwnerId(itemDto.getId(), itemDto.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Item with id " + itemDto.getId() + " is not exist"));

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
    public ItemAnswerDto getItem(long itemId,long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not exist"));

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
    public List<ItemAnswerDto> getItems(Long ownerId) {

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
    public CommentAnswerDto createComment(CommentPostRequestDto commentDto) {

        bookingRepository.findLastBookedByBookerIdAndItemId(commentDto.getAuthorId(),
                commentDto.getItemId()).orElseThrow(() ->
                new NotAvailableException(
                        String.format("item_id %s should be booked by user_id %s",
                                commentDto.getItemId(), commentDto.getAuthorId())));

        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow(
                () -> new NotFoundException(
                        String.format("item_Id %s is not found", commentDto.getItemId())));

        User author = userMapper.toModel(userService.getUserById(commentDto.getAuthorId()));

        Comment comment = Comment.builder()
                .item(item)
                .author(author)
                .text(commentDto.getText())
                .build();
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
