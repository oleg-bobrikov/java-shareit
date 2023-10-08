package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingStateUnknownException;
import ru.practicum.shareit.exception.PeriodValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateUnknownException("Unknown state: " + stateParam));
        log.info("Get bookings by user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        validatePeriod(requestDto.getStart(), requestDto.getEnd());
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("approve booking with bookingId={}, userId={}", bookingId, ownerId);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "32") @Min(1) int size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateUnknownException("Unknown state: " + stateParam));
        log.info("Get bookings by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwnerId(userId, state, from, size);
    }

    private void validatePeriod(LocalDateTime startDate, LocalDateTime endDate) {

        if (endDate.isBefore(startDate)) {
            throw new PeriodValidationException("end is before start");
        }

        if (startDate.equals(endDate)) {
            throw new PeriodValidationException("start equals end");
        }
    }
}