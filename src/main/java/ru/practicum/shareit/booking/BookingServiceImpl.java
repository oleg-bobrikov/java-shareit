package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    public BookingAnswerDto createBooking(BookingRequestDto bookingRequestDto) {

        Instant startDate = InstantConverter.fromPattern(bookingRequestDto.getStart());
        Instant endDAte = InstantConverter.fromPattern(bookingRequestDto.getEnd());

        validatePeriod(startDate, endDAte);

        User booker = findUserByIdLockRead(bookingRequestDto.getBookerId());
        Item item = findByIdLockRead(bookingRequestDto.getItemId());

        if (!item.getIsAvailable()) {
            throw new NotAvailableException("Item with id " + bookingRequestDto.getItemId() + " is not available for booking");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Booker and owner should be different");
        }

        // search existing bookings for the item with intersection periods having status Waiting or Approved
        List<Booking> conflicts = bookingRepository.findIntersectionPeriods(item.getId(),
                OffsetDateTime.ofInstant(startDate, ZoneId.of("UTC")),
                OffsetDateTime.ofInstant(endDAte, ZoneId.of("UTC")));

        if (!conflicts.isEmpty()) {
            throw new NotAvailableException("Item with id " + item.getId() + " has already booked");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .startDate(OffsetDateTime.ofInstant(startDate, ZoneId.of("UTC")))
                .endDate(OffsetDateTime.ofInstant(endDAte, ZoneId.of("UTC")))
                .build();

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    private void validatePeriod(Instant startDate, Instant endDate) {
        Instant now = Instant.now();
        if (startDate.isBefore(now)) {
            throw new PeriodValidationException("start is in the past");
        }

        if (endDate.isBefore(startDate)) {
            throw new PeriodValidationException("end is before start");
        }

        if (startDate.equals(endDate)) {
            throw new PeriodValidationException("start equals end");
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingAnswerDto approve(BookingApproveDto bookingApproveDto) {

        Booking booking = findBookingById(bookingApproveDto.getBookingId());

        if (!booking.getItem().getOwner().getId().equals(bookingApproveDto.getOwnerId())) {
            throw new NotFoundException("Booking with id " + bookingApproveDto.getBookingId() + " should have valid owner ID");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new UnsupportedStatusException("Wrong status for booking with id " + bookingApproveDto.getBookingId());
        } else if (bookingApproveDto.getApproved()) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    private User findUserByIdLockRead(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not exist"));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " is not exist"));
    }

    private Item findByIdLockRead(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " is not exist"));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingAnswerDto getBooking(BookingGetDto bookingGetDto) {

        User user = findUserByIdLockRead(bookingGetDto.getUserId());

        Booking booking = findBookingById(bookingGetDto.getBookingId());

        long ownerId = booking.getItem().getOwner().getId();
        long userId = user.getId();
        long bookerId = booking.getBooker().getId();
        if (!(userId == ownerId || userId == bookerId)) {
            throw new NotFoundException("User with id " + bookingGetDto.getUserId() + " is not valid.");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public List<BookingAnswerDto> getBookingsByBookerId(long bookerId, State state, int from, int size) {

        findUserByIdLockRead(bookerId);

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByBookerIdOrderByStartDateDesc(bookerId, page));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByBookerIdOrderByStartDateAsc(bookerId, page));
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
    public List<BookingAnswerDto> getBookingsByOwnerId(long ownerId, State state, int from, int size) {

        findUserByIdLockRead(ownerId);

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
