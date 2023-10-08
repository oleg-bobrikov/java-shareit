package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemAnswerDto createItem(long ownerId, ItemRequestDto itemRequestRequestDto);

    ItemAnswerDto updateItem(long ownerId, ItemRequestDto itemRequestRequestDto, long itemId);

    ItemAnswerDto getItem(long itemId, long userId);

    List<ItemAnswerDto> getItemsByOwnerId(Long ownerId);

    List<ItemAnswerDto> searchItems(String text);

    CommentAnswerDto createComment(long authorId, CommentRequestDto commentDto, long itemId);
}
