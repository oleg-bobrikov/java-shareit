package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.util.Generator;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.Constant.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @SneakyThrows
    @Test
    void createBooking_validBookingDto_returnStatusOkAndValidJson() {
        // Arrange
        Item item = Generator.makeItemWithOwner();
        BookingRequestDto requestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        BookingAnswerDto expected = BookingAnswerDto.builder()
                .id(1L)
                .item(itemMapper.toHeaderDto(item))
                .status(Status.WAITING)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .booker(userMapper.toHeaderDto(booker))
                .build();

        when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    BookingRequestDto requestDto1 = invocationOnMock.getArgument(1, BookingRequestDto.class);
                    return BookingAnswerDto.builder()
                            .id(1L)
                            .start(requestDto1.getStart())
                            .end(requestDto1.getEnd())
                            .booker(userMapper.toHeaderDto(booker))
                            .item(itemMapper.toHeaderDto(item))
                            .status(Status.WAITING)
                            .build();
                });

        // Act and assert
        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, bookerId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

    }

    @SneakyThrows
    @Test
    void patchBooking_validOwnerIdAndBookingId_returnStatusOkAndValidJson() {
        // Arrange
        Item item = Generator.makeItemWithOwner();
        BookingRequestDto requestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookingId = 1L;
        long ownerId = item.getOwner().getId();
        BookingAnswerDto expected = BookingAnswerDto.builder()
                .id(bookingId)
                .item(itemMapper.toHeaderDto(item))
                .status(Status.APPROVED)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .booker(userMapper.toHeaderDto(booker))
                .build();

        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenAnswer(invocationOnMock -> {
                    long bookingId1 = invocationOnMock.getArgument(1, Long.class);

                    return BookingAnswerDto.builder()
                            .id(bookingId1)
                            .status(Status.APPROVED)
                            .booker(userMapper.toHeaderDto(booker))
                            .item(itemMapper.toHeaderDto(item))
                            .start(requestDto.getStart())
                            .end(requestDto.getEnd())
                            .build();
                });

        // Act and assert
        mvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header(USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

    }

    @SneakyThrows
    @Test
    void getBooking_validUserId_returnStatusOkAndValidJson() {
        // Arrange
        Item item = Generator.makeItemWithOwner();
        BookingRequestDto requestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookingId = 1L;
        long ownerId = item.getOwner().getId();
        BookingAnswerDto expected = BookingAnswerDto.builder()
                .id(bookingId)
                .item(itemMapper.toHeaderDto(item))
                .status(Status.APPROVED)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .booker(userMapper.toHeaderDto(booker))
                .build();

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    long bookingId1 = invocationOnMock.getArgument(1, Long.class);

                    return BookingAnswerDto.builder()
                            .id(bookingId1)
                            .status(Status.APPROVED)
                            .booker(userMapper.toHeaderDto(booker))
                            .item(itemMapper.toHeaderDto(item))
                            .start(requestDto.getStart())
                            .end(requestDto.getEnd())
                            .build();
                });

        // Act and assert
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_validOwnerId_returnStatusOkAndValidJson() {
        // Arrange
        Item item = Generator.makeItemWithOwner();
        BookingRequestDto requestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookingId = 1L;
        long ownerId = item.getOwner().getId();
        List<BookingAnswerDto> expected = Collections.singletonList(BookingAnswerDto.builder()
                .id(bookingId)
                .item(itemMapper.toHeaderDto(item))
                .status(Status.APPROVED)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .booker(userMapper.toHeaderDto(booker))
                .build());

        when(bookingService.getBookingsByOwnerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(BookingAnswerDto.builder()
                        .id(bookingId)
                        .status(Status.APPROVED)
                        .booker(userMapper.toHeaderDto(booker))
                        .item(itemMapper.toHeaderDto(item))
                        .start(requestDto.getStart())
                        .end(requestDto.getEnd())
                        .build()));

        // Act and assert
        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", 0, 32)
                        .header(USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_validBookerId_returnStatusOkAndValidJson() {
        // Arrange
        Item item = Generator.makeItemWithOwner();
        BookingRequestDto requestDto = Generator.makeBookingRequestDtoNextDay();
        User booker = Generator.makeUser2();
        long bookerId = booker.getId();
        long bookingId = 1L;
        List<BookingAnswerDto> expected = Collections.singletonList(BookingAnswerDto.builder()
                .id(bookingId)
                .item(itemMapper.toHeaderDto(item))
                .status(Status.APPROVED)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .booker(userMapper.toHeaderDto(booker))
                .build());

        when(bookingService.getBookingsByBookerId(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(BookingAnswerDto.builder()
                        .id(bookingId)
                        .status(Status.APPROVED)
                        .booker(userMapper.toHeaderDto(booker))
                        .item(itemMapper.toHeaderDto(item))
                        .start(requestDto.getStart())
                        .end(requestDto.getEnd())
                        .build()));

        // Act and assert
        mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "ALL", 0, 32)
                        .header(USER_ID_HEADER, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

}