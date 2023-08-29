package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.exception.BusinessLogicException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingAnswerDto createBooking(BookingRequestDto bookingRequestDto) {

        Instant start = InstantConverter.fromPattern(bookingRequestDto.getStart());
        Instant end = InstantConverter.fromPattern(bookingRequestDto.getEnd());
        Instant now = Instant.now();
        if (start.isBefore(now)) {
            throw new BusinessLogicException("start is in the past");
        }

        if (end.isBefore(start)) {
            throw new BusinessLogicException("end is before start");
        }

        if (start.equals(end)) {
            throw new BusinessLogicException("start equals end");
        }

        Item item = itemRepository.findByIdPessimisticRead(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingRequestDto.getItemId() + " is not exist"));

        User booker = userRepository.findByIdPessimisticRead(bookingRequestDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker with id " + bookingRequestDto.getBookerId() + " is not exist"));

        if (!item.getIsAvailable()) {
            throw new NotAvailableException("Item with id " + bookingRequestDto.getItemId() + " is not available for booking");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingAnswerDto approve(BookingApproveDto bookingApproveDto) {
        Booking booking = bookingRepository.findById(bookingApproveDto.getBookingId())
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingApproveDto.getBookingId() + " is not exist"));
        if (!booking.getItem().getOwner().getId().equals(bookingApproveDto.getOwnerId())) {
            throw new BusinessLogicException("Booking with id " + bookingApproveDto.getBookingId() + " should have valid owner ID");
        }
        if (booking.getStatus().equals(Status.WAITING) && bookingApproveDto.getApproved()) {
            booking.setStatus(Status.APPROVED);
        } else if (!booking.getStatus().equals(Status.APPROVED) && !bookingApproveDto.getApproved()) {
            booking.setStatus(Status.REJECTED);
        } else {
            return bookingMapper.toDto(booking);
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingAnswerDto getBooking(BookingGetDto bookingGetDto) {
        Booking booking = bookingRepository.findById(bookingGetDto.getBookingId())
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingGetDto.getBookingId() + " is not exist"));

        if (!booking.getItem().getOwner().getId().equals(bookingGetDto.getUserId()) &&
                !booking.getBooker().getId().equals(bookingGetDto.getUserId())) {
            throw new BusinessLogicException("User with id " + bookingGetDto.getUserId() + " is not valid.");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public List<BookingAnswerDto> getBookingsByBookerId(Long bookerId, State state) {

        userRepository.findByIdPessimisticRead(bookerId)
                .orElseThrow(() -> new NotFoundException("Booker with id " + bookerId + " is not exist"));

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByBookerIdOrderByStartDesc(bookerId));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByBookerIdOrderByStartDesc(bookerId));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByBookerIdOrderByStartDesc(bookerId));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByBookerIdOrderByStartDesc(bookerId));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByIdAndStatusOrderByStartDesc(bookerId, Status.WAITING));
            case REJECTED:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED));
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public List<BookingAnswerDto> getBookingsByOwnerId(Long ownerId, State state) {
        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByOwnerIdOrderByStartDesc(ownerId));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByOwnerIdOrderByStartDesc(ownerId));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByOwnerIdOrderByStartDesc(ownerId));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByOwnerIdOrderByStartDesc(ownerId));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, Status.WAITING));
            case REJECTED:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, Status.REJECTED));
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }
}
