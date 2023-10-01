package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PeriodValidationException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.Generator;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, itemRepository, userRepository);
    }

    @Test
    void getBooking_userExistAndBookingExist_returnDto() {
        // Arrange
        User user = Generator.makeUser1();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = Generator.makeBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act
        BookingAnswerDto actual = bookingService.getBooking(user.getId(), booking.getId());

        // Assert
        assertEquals(actual, bookingMapper.toDto(booking));
    }

    @Test
    void getBooking_userNotAllowed_returnDto() {
        // Arrange
        User user = Generator.makeUser1();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        long wrongUserId = 100L;

        Booking booking = Generator.makeBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(wrongUserId, booking.getId()));

    }

    @Test
    void createBooking_bookerExistValidPeriodAndBookingRequestDto_returnDto() {
        // Arrange
        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        Item item = Generator.makeItemWithOwner();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findIntersectionPeriods(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(new ArrayList<>());

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> {
                    Booking booking1 = invocationOnMock.getArgument(0, Booking.class);
                    booking1.setId(1L);
                    return booking1;
                });

        BookingAnswerDto expected = BookingAnswerDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .status(Status.WAITING)
                .booker(booker)
                .build();

        // Act
        BookingAnswerDto actual = bookingService.createBooking(bookerId, bookingRequestDto);

        // Assert
        assertEquals(expected, actual);

        verify(bookingRepository, times(1)).save(any(Booking.class));

    }

    @Test
    void createBooking_itemNotAvailable_throwNotAvailableException() {
        // Arrange
        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        Item item = Generator.makeItemWithOwner();
        item.setIsAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Act and assert
        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_itemNotExist_throwNotFoundException() {
        // Arrange
        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_bookerIsEqualOwner_throwNotFoundException() {
        // Arrange
        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser1();
        long bookerId = booker.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        Item item = Generator.makeItemWithOwner();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_itemHasBooked_throwNotAvailableException() {
        // Arrange
        BookingRequestDto bookingRequestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        Item item = Generator.makeItemWithOwner();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findIntersectionPeriods(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.singletonList(Generator.makeBooking()));

        // Act and assert
        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_startBeforeNow_throwPeriodValidationException() {
        // Arrange
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(InstantConverter.toPattern(Instant.now().minusSeconds(3600)))
                .end(InstantConverter.toPattern(Instant.now().plusSeconds(3600)))
                .build();
        long bookerId = 1;

        // Act and assert
        assertThrows(PeriodValidationException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_endBeforeStart_throwPeriodValidationException() {
        // Arrange
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(InstantConverter.toPattern(Instant.now().plusSeconds(3600)))
                .end(InstantConverter.toPattern(Instant.now().minusSeconds(3600)))
                .build();
        long bookerId = 1;

        // Act and assert
        assertThrows(PeriodValidationException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void createBooking_startEquealsEnd_throwPeriodValidationException() {
        // Arrange
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(InstantConverter.toPattern(Instant.now().plusSeconds(3600)))
                .end(InstantConverter.toPattern(Instant.now().plusSeconds(3600)))
                .build();
        long bookerId = 1;

        // Act and assert
        assertThrows(PeriodValidationException.class, () -> bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @Test
    void approve_bookingExist_returnDto() {
        // Arrange
        Booking booking = Generator.makeBooking();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        boolean approved = true;
        long bookingId = booking.getId();
        long ownerId = booking.getItem().getOwner().getId();

        // Act
        BookingAnswerDto actual = bookingService.approve(ownerId, bookingId, approved);

        // Assert
        assertThat(actual).hasFieldOrPropertyWithValue("status", Status.APPROVED);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approve_bookingNotExist_throwNotFoundException() {
        // Arrange
        Booking booking = Generator.makeBooking();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean approved = true;
        long bookingId = booking.getId();
        long ownerId = booking.getItem().getOwner().getId();

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.approve(ownerId, bookingId, approved));
    }


    @Test
    void approve_wrongStatus_throwUnsupportedStatusException() {
        // Arrange
        Booking booking = Generator.makeBooking();
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        boolean approved = true;
        long bookingId = booking.getId();
        long ownerId = booking.getItem().getOwner().getId();

        // Act and assert
        assertThrows(UnsupportedStatusException.class, () -> bookingService.approve(ownerId, bookingId, approved));
    }

    @Test
    void approve_notApproved_returnDtoWithStatusRejected() {
        // Arrange
        Booking booking = Generator.makeBooking();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        boolean approved = false;
        long bookingId = booking.getId();
        long ownerId = booking.getItem().getOwner().getId();

        // Act
        BookingAnswerDto actual = bookingService.approve(ownerId, bookingId, approved);

        // Assert
        assertThat(actual).hasFieldOrPropertyWithValue("status", Status.REJECTED);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approve_ownerNotAllowed_throwNotFoundException() {
        // Arrange
        Booking booking = Generator.makeBooking();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        boolean approved = false;
        long bookingId = booking.getId();
        long ownerId = booking.getItem().getOwner().getId() + 100L;

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.approve(ownerId, bookingId, approved));
    }

    @Test
    void getBookingsByBookerId_userNotFound_throwNotFoundException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        State state = State.ALL;
        long bookerId = 1L;
        int from = 0;
        int size = 20;

        // Act and assert
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByBookerId(bookerId, state, from, size));
    }

    @Test
    void getBookingsByOwnerId_stateAll_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAllByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.ALL;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAllByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerId_stateCurrent_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findCurrentByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.CURRENT;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findCurrentByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerId_statePast_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findPastByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.PAST;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findPastByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerId_stateFuture_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findFutureByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.FUTURE;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findFutureByOwnerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerId_stateWaiting_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));

        State state = State.WAITING;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerId_stateRejected_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));

        State state = State.REJECTED;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByOwnerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_stateAll_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAllByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.ALL;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_stateCurrent_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findCurrentByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.CURRENT;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findCurrentByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_statePast_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findPastByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.PAST;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findPastByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_stateFuture_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findFutureByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));

        State state = State.FUTURE;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findFutureByBookerIdOrderByStartDateDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_stateWaiting_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAlByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));

        State state = State.WAITING;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAlByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getBookingsByBookerId_stateRejected_returnListDto() {
        // Arrange
        Booking booking = Generator.makeBooking();
        User booker = booking.getBooker();
        long bookerId = booker.getId();

        doReturn(Optional.of(booker)).when(userRepository).findById(anyLong());
        doReturn(Collections.singletonList(booking)).when(bookingRepository)
                .findAlByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));

        State state = State.REJECTED;
        int from = 0;
        int size = 20;

        // Act
        List<BookingAnswerDto> actual = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        // Assert
        assertThat(actual).asList().hasSize(1);

        verify(bookingRepository, times(1))
                .findAlByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), any(Status.class), any(Pageable.class));
    }


}