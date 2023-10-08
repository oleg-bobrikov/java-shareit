package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    @SneakyThrows
    void createBooking_startBeforeNow_returnStatusBadRequest() {

        // Arrange
        long bookerId = 1L;
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        // Act and assert
        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", bookerId)
                .content(mapper.writeValueAsString(requestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBooking_endBeforeStart_returnStatusBadRequest() {
        // Arrange
        long bookerId = 1L;
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().minusHours(1))
                .build();

        // Act and assert
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}