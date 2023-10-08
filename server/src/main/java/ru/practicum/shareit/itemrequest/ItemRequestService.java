package ru.practicum.shareit.itemrequest;

import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestAnswerDto createRequest(long userId, ItemRequestRequestDto itemRequestRequestDto);

    List<ItemRequestAnswerDto> getAllByOthers(long userId, int from, int size);

    ItemRequestAnswerDto getById(long id, long userId);

    List<ItemRequestAnswerDto> getRequestsByOwner(long userId);
}
