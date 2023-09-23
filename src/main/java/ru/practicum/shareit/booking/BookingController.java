package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingAnswerDto createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                          @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        bookingRequestDto.setBookerId(bookerId);
        return bookingService.createBooking(bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingAnswerDto patchBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PathVariable long bookingId,
                                         @RequestParam boolean approved) {
        BookingApproveDto bookingApproveDto = BookingApproveDto.builder()
                .ownerId(ownerId)
                .bookingId(bookingId)
                .approved(approved)
                .build();
        return bookingService.approve(bookingApproveDto);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingAnswerDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long bookingId) {
        BookingGetDto bookingGetDto = BookingGetDto.builder()
                .userId(userId)
                .bookingId(bookingId)
                .build();
        return bookingService.getBooking(bookingGetDto);
    }

    @GetMapping(path = "/owner")
    public List<BookingAnswerDto> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        return bookingService.getBookingsByOwnerId(ownerId, StateConverter.toState(state), from, size);
    }

    @GetMapping()
    public List<BookingAnswerDto> getBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        return bookingService.getBookingsByBookerId(bookerId, StateConverter.toState(state), from, size);
    }

}
