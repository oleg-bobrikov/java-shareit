package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_createEntityOnService_returnSameEntityFromRepository() {
        // Arrange
        UserShortDto ownerShortDto = Generator.makeUserShortDto1();
        UserDto ownerDto = userService.createUser(ownerShortDto);
        final long ownerId = ownerDto.getId();

        UserShortDto bookerShortDto = Generator.makeUserShortDto2();
        UserDto bookerDto = userService.createUser(bookerShortDto);
        final long bookerId = bookerDto.getId();

        ItemPostRequestDto itemPostRequestDto = Generator.makeItemPostRequestDtoWithoutRequestId();
        ItemAnswerDto itemAnswerDto = itemService.createItem(ownerId, itemPostRequestDto);
        final long itemId = itemAnswerDto.getId();

        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        bookingRequestDto.setItemId(itemId);

        // Act
        BookingAnswerDto actual = bookingService.createBooking(bookerId, bookingRequestDto);
        Optional<Booking> createdBooking = bookingRepository.findById(actual.getId());

        // Assert
        assertThat(createdBooking).isPresent()
                .satisfies(booking -> assertThat(booking.get())
                        .hasFieldOrPropertyWithValue("id", actual.getId())
                        .hasFieldOrPropertyWithValue("item", actual.getItem())
                        .hasFieldOrPropertyWithValue("status", actual.getStatus())
                        .hasFieldOrPropertyWithValue("booker", actual.getBooker()));

    }
}

