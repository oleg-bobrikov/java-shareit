package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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
    public ItemAnswerDto getItem(ItemGetRequestDto itemGetRequestDto) {
        Item item = itemRepository.findById(itemGetRequestDto.getId())
                .orElseThrow(() -> new NotFoundException("Item with id " + itemGetRequestDto.getId() + " is not exist"));

        ItemAnswerDto itemAnswerDto = itemMapper.toDto(item);

        if (item.getOwner().getId().equals(itemGetRequestDto.getUserId())) {

            Booking lastBooking = bookingRepository.findLastByItemId(item.getId()).orElse(null);
            itemAnswerDto.setLastBooking(bookingMapper.toShortDto(lastBooking));

            Booking nextBooking = bookingRepository.findFutureByItemId(item.getId()).orElse(null);
            itemAnswerDto.setNextBooking(bookingMapper.toShortDto(nextBooking));
        }

        return itemAnswerDto;
    }

    @Override
    public List<ItemAnswerDto> getItems(Long ownerId) {

        return itemRepository.findByOwnerId(ownerId).stream().map(item -> {

            ItemAnswerDto itemAnswerDto = itemMapper.toDto(item);
            Booking lastBooking = bookingRepository.findLastByItemId(item.getId()).orElse(null);
            itemAnswerDto.setLastBooking(bookingMapper.toShortDto(lastBooking));

            Booking nextBooking = bookingRepository.findFutureByItemId(item.getId()).orElse(null);
            itemAnswerDto.setNextBooking(bookingMapper.toShortDto(nextBooking));

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
}
