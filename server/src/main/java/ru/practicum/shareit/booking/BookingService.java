package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

public interface BookingService {
    BookingAnswerDto createBooking(long bookerId, BookingRequestDto bookingRequestDto);

    BookingAnswerDto approve(long ownerId, long bookingId, boolean approved);

    BookingAnswerDto getBooking(long userId, long bookingId);

    List<BookingAnswerDto> getBookingsByBookerId(long bookerId, BookingState state, int from, int size);

    List<BookingAnswerDto> getBookingsByOwnerId(long ownerId, BookingState state, int from, int size);
}
