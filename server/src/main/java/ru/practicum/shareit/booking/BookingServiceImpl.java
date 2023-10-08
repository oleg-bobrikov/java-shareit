package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingAnswerDto createBooking(long bookerId, BookingRequestDto bookingRequestDto) {

        LocalDateTime startDate = bookingRequestDto.getStart();
        LocalDateTime endDate = bookingRequestDto.getEnd();

        User booker = findUserById(bookerId);
        Item item = findItemById(bookingRequestDto.getItemId());

        if (!item.getIsAvailable()) {
            throw new NotAvailableException("Item with id " + bookingRequestDto.getItemId() + " is not available for booking");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Booker and owner should be different");
        }

        // search existing bookings for the item with intersection periods having status Waiting or Approved
        List<Booking> conflicts = bookingRepository.findIntersectionPeriods(item.getId(), startDate, endDate);

        if (!conflicts.isEmpty()) {
            throw new NotAvailableException("Item with id " + item.getId() + " has already booked");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return bookingMapper.toDto(bookingRepository.save(booking));
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingAnswerDto approve(long ownerId, long bookingId, boolean approved) {

        Booking booking = findBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Booking with id " + bookingId + " should have valid owner ID");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new UnsupportedStatusException("Wrong status for booking with id " + bookingId);
        } else if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not exist"));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " is not exist"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not exist"));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingAnswerDto getBooking(long userId, long bookingId) {

        findUserById(userId);

        Booking booking = findBookingById(bookingId);

        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (!(userId == ownerId || userId == bookerId)) {
            throw new NotFoundException("User with id " + userId + " is not valid.");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public List<BookingAnswerDto> getBookingsByBookerId(long bookerId, BookingState state, int from, int size) {

        findUserById(bookerId);

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByBookerIdOrderByStartDateDesc(bookerId, page));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByBookerIdOrderByStartDateDesc(bookerId, page));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByBookerIdOrderByStartDateDesc(bookerId, page));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByBookerIdOrderByStartDateDesc(bookerId, page));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAlByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.WAITING, page));
            case REJECTED:
            default:
                return bookingMapper.toDtoList(bookingRepository
                        .findAlByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.REJECTED, page));
        }
    }

    @Override
    @Transactional
    public List<BookingAnswerDto> getBookingsByOwnerId(long ownerId, BookingState state, int from, int size) {

        findUserById(ownerId);

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByOwnerIdOrderByStartDateDesc(ownerId, page));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByOwnerIdOrderByStartDateDesc(ownerId, page));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByOwnerIdOrderByStartDateDesc(ownerId, page));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByOwnerIdOrderByStartDateDesc(ownerId, page));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.WAITING, page));
            case REJECTED:
            default:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.REJECTED, page));
        }
    }
}
