package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingAnswerDto createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                          @RequestBody BookingRequestDto bookingRequestDto) {

        return bookingService.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingAnswerDto patchBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PathVariable long bookingId,
                                         @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingAnswerDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping(path = "/owner")
    public List<BookingAnswerDto> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size) {

        return bookingService.getBookingsByOwnerId(ownerId, state, from, size);
    }

    @GetMapping()
    public List<BookingAnswerDto> getBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size) {

        return bookingService.getBookingsByBookerId(bookerId, state, from, size);
    }

}