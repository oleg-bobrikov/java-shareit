package ru.practicum.shareit.itemrequest;

import ru.practicum.shareit.itemrequest.dto.AnswerDto;
import ru.practicum.shareit.itemrequest.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    AnswerDto createRequest(long userId, RequestDto requestDto);

    List<AnswerDto> getAllByOthers(long userId, int from, int size);

    AnswerDto getById(long id, long userId);

    List<AnswerDto> getRequestsByOwner(long userId);
}
