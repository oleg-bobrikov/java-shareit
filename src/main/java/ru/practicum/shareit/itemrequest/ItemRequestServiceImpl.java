package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
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
    public ItemRequestAnswerDto createRequest(long userId, ItemRequestRequestDto itemRequestRequestDto) {
        User user = findUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .description(itemRequestRequestDto.getDescription())
                .build();
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestAnswerDto> getRequestsByOwner(long requesterId) {
        findUserById(requesterId);
        return itemRequestRepository.findByRequesterIdOrderByCreatedDateDesc(requesterId).stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestAnswerDto> getAllByOthers(long requesterId, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findOthersByRequesterIdOrderByCreatedDateDesc(requesterId, page)
                .getContent()
                .stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestAnswerDto getById(long id, long userId) {
        findUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request with id " + id + " not found."));

        return setItems(itemRequestMapper.toDto(itemRequest));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found."));
    }

    private ItemRequestAnswerDto setItems(ItemRequestAnswerDto itemRequestAnswerDto) {
        List<Item> items = itemRepository.findByItemRequestId(itemRequestAnswerDto.getRequester().getId());
        itemRequestAnswerDto.setItems(itemMapper.toShortDto(items));
        return itemRequestAnswerDto;
    }
}
