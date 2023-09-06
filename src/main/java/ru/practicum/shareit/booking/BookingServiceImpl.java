package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingRequestDto.getItemId() + " is not exist"));

        User booker = findUserById(bookingRequestDto.getBookerId());

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

    private User findUserById(long userId) {
        return userRepository.findByIdPessimisticRead(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not exist"));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " is not exist"));
    }

    @Override
    @Transactional
    public BookingAnswerDto getBooking(BookingGetDto bookingGetDto) {

        User user = findUserById(bookingGetDto.getUserId());

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
    public List<BookingAnswerDto> getBookingsByBookerId(Long bookerId, State state) {

        findUserById(bookerId);

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByBookerIdOrderByStartDateDesc(bookerId));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByBookerIdOrderByStartDateAsc(bookerId));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByBookerIdOrderByStartDateDesc(bookerId));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByBookerIdOrderByStartDateDesc(bookerId));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAlByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.WAITING));
            case REJECTED:
                return bookingMapper.toDtoList(bookingRepository
                        .findAlByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.REJECTED));
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    @Transactional
    public List<BookingAnswerDto> getBookingsByOwnerId(Long ownerId, State state) {

        findUserById(ownerId);

        switch (state) {
            case ALL:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByOwnerIdOrderByStartDateDesc(ownerId));
            case CURRENT:
                return bookingMapper.toDtoList(bookingRepository
                        .findCurrentByOwnerIdOrderByStartDateDesc(ownerId));
            case PAST:
                return bookingMapper.toDtoList(bookingRepository
                        .findPastByOwnerIdOrderByStartDateDesc(ownerId));
            case FUTURE:
                return bookingMapper.toDtoList(bookingRepository
                        .findFutureByOwnerIdOrderByStartDateDesc(ownerId));
            case WAITING:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.WAITING));
            case REJECTED:
                return bookingMapper.toDtoList(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.REJECTED));
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }
}
