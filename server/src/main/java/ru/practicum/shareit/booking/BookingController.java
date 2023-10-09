package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import java.util.List;

import static ru.practicum.shareit.common.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingAnswerDto createBooking(@RequestHeader(USER_ID_HEADER) long bookerId,
                                          @RequestBody BookingRequestDto bookingRequestDto) {

        return bookingService.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingAnswerDto patchBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                         @PathVariable long bookingId,
                                         @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingAnswerDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                       @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping(path = "/owner")
    public List<BookingAnswerDto> getBookingsByOwnerId(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size) {

        return bookingService.getBookingsByOwnerId(ownerId, state, from, size);
    }

    @GetMapping()
    public List<BookingAnswerDto> getBookingsByBookerId(
            @RequestHeader(USER_ID_HEADER) long bookerId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size) {

        return bookingService.getBookingsByBookerId(bookerId, state, from, size);
    }

}
