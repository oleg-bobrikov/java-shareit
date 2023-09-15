package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

public interface BookingService {
    BookingAnswerDto createBooking(BookingRequestDto bookingRequestDto);

    BookingAnswerDto approve(BookingApproveDto bookingApproveDto);

    BookingAnswerDto getBooking(BookingGetDto bookingGetDto);

    List<BookingAnswerDto> getBookingsByBookerId(long bookerId, State state, int from, int size);

    List<BookingAnswerDto> getBookingsByOwnerId(long ownerId, State state, int from, int size);
}
