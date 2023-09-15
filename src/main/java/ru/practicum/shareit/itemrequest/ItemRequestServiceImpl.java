package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.itemrequest.dto.AnswerDto;
import ru.practicum.shareit.itemrequest.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public AnswerDto createRequest(long userId, RequestDto requestDto) {
        User user = findUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .description(requestDto.getDescription())
                .build();
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<AnswerDto> getRequestsByOwner(long requesterId) {
        findUserById(requesterId);
        return itemRequestRepository.findByRequesterIdOrderByCreatedDateDesc(requesterId).stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnswerDto> getAllByOthers(long requesterId, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findOthersByRequesterIdOrderByCreatedDateDesc(requesterId, page)
                .getContent()
                .stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public AnswerDto getById(long id, long userId) {
        findUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request with id " + id + " not found."));

        return setItems(itemRequestMapper.toDto(itemRequest));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found."));
    }

    private AnswerDto setItems(AnswerDto answerDto) {
        List<Item> items = itemRepository.findByItemRequestId(answerDto.getRequester().getId());
        answerDto.setItems(itemMapper.toShortDto(items));
        return answerDto;
    }
}
