package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Generator;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-h2.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    @DirtiesContext
    void createBooking_createEntityOnService_returnSameEntityFromRepository() {
        // Arrange
        UserRequestDto ownerShortDto = Generator.makeUserRequestDto1();
        UserDto ownerDto = userService.createUser(ownerShortDto);
        final long ownerId = ownerDto.getId();

        UserRequestDto bookerShortDto = Generator.makeUserRequestDto2();
        UserDto bookerDto = userService.createUser(bookerShortDto);
        final long bookerId = bookerDto.getId();

        ItemRequestDto itemRequestDto = Generator.makeItemRequestDtoWithoutRequestId();
        ItemAnswerDto itemAnswerDto = itemService.createItem(ownerId, itemRequestDto);
        final long itemId = itemAnswerDto.getId();

        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        bookingRequestDto.setItemId(itemId);

        // Act
        BookingAnswerDto actual = bookingService.createBooking(bookerId, bookingRequestDto);
        Optional<Booking> createdBooking = bookingRepository.findById(actual.getId());

        // Assert
        assertThat(createdBooking).isPresent()
                .satisfies(bookingOptional -> assertThat(bookingOptional.get())
                        .hasFieldOrPropertyWithValue("id", actual.getId())
                        .hasFieldOrPropertyWithValue("status", actual.getStatus()))
                .satisfies(booking -> assertThat(booking.get().getItem())
                        .hasFieldOrPropertyWithValue("id", actual.getItem().getId())
                        .hasFieldOrPropertyWithValue("name", actual.getItem().getName()))
                .satisfies(booking -> assertThat(booking.get().getBooker())
                        .hasFieldOrPropertyWithValue("id", actual.getBooker().getId()));
    }
}

