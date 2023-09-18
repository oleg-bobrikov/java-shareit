package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.itemrequest.dto.ItemAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
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
    public ItemAnswerDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = findUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .description(itemRequestDto.getDescription())
                .build();
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemAnswerDto> getRequestsByOwner(long requesterId) {
        findUserById(requesterId);
        return itemRequestRepository.findByRequesterIdOrderByCreatedDateDesc(requesterId).stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemAnswerDto> getAllByOthers(long requesterId, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findOthersByRequesterIdOrderByCreatedDateDesc(requesterId, page)
                .getContent()
                .stream()
                .map(itemRequestMapper::toDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemAnswerDto getById(long id, long userId) {
        findUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request with id " + id + " not found."));

        return setItems(itemRequestMapper.toDto(itemRequest));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found."));
    }

    private ItemAnswerDto setItems(ItemAnswerDto itemAnswerDto) {
        List<Item> items = itemRepository.findByItemRequestId(itemAnswerDto.getRequester().getId());
        itemAnswerDto.setItems(itemMapper.toShortDto(items));
        return itemAnswerDto;
    }
}
