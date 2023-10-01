package ru.practicum.shareit.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.converter.InstantConverter;

import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Generator;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @InjectMocks
    private ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    ItemRequestService itemRequestService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @SneakyThrows
    void getById_requestExist_returnOkAndDto() {
        // arrange
        long requestId = 1;
        long userId = 1L;
        ItemRequestAnswerDto expected = Generator.makeItemRequestAnswerDto();

        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(expected);

        // act and assert
        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expected.getDescription())))
                .andExpect(jsonPath("$.created", is(expected.getCreated())));

        verify(itemRequestService, times(1)).getById(requestId, userId);
    }

    @Test
    @SneakyThrows
    void getAll_requestExist_returnOkAndDto() {
        // Arrange
        long userId = 1L;
        ItemRequestAnswerDto request = Generator.makeItemRequestAnswerDto();
        List<ItemRequestAnswerDto> expected = Collections.singletonList(request);

        when(itemRequestService.getAllByOthers(anyLong(), anyInt(), anyInt())).thenReturn(expected);

        // Act and assert
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated())));

        verify(itemRequestService, times(1)).getAllByOthers(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getRequestsByOwner_ownerExist_returnOkAndDto() {
        // Arrange
        long userId = 1L;
        ItemRequestAnswerDto request = Generator.makeItemRequestAnswerDto();
        List<ItemRequestAnswerDto> expected = Collections.singletonList(request);

        when(itemRequestService.getRequestsByOwner(anyLong())).thenReturn(expected);

        // Act and assert
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated())));

        verify(itemRequestService, times(1)).getRequestsByOwner(anyLong());
    }

    @Test
    @SneakyThrows
    void createRequest_userExist_returnOkAndDto() {
        // Arrange
        User requester = Generator.makeUser2();
        long userId = 1L;
        ItemRequestRequestDto requestDto = Generator.makeItemRequestRequestDto();
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestRequestDto requestDto1 = invocationOnMock.getArgument(1, ItemRequestRequestDto.class);
                    return ItemRequestAnswerDto.builder()
                            .requester(requester)
                            .id(1L)
                            .created(InstantConverter.toPatternMillis(Instant.now()))
                            .description(requestDto1.getDescription())
                            .build();
                });

        // Act and assert
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));


        verify(itemRequestService, times(1)).createRequest(anyLong(), any(ItemRequestRequestDto.class));
    }

}