package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.util.Generator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.Constant.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private ItemController controller;

    @Mock
    private ItemService itemService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @SneakyThrows
    public void createItem_validRequest_returnOk() {
        // arrange
        ItemRequestDto request = Generator.makeItemRequestDtoWithoutRequestId();
        long itemId = 1L;
        long userId = 1L;

        when(itemService.createItem(anyLong(), any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemAnswerDto answerDto = itemMapper.toDto(invocationOnMock.getArgument(1, ItemRequestDto.class));
                    answerDto.setId(itemId);
                    return answerDto;
                });

        // act and assert
        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.available", is(request.getAvailable())));

        verify(itemService, times(1)).createItem(userId, request);
    }

    @Test
    public void createComment_validRequest_returnOk() throws Exception {
        // arrange
        long itemId = 1L;
        long authorId = 1L;
        long commentId = 1L;
        CommentRequestDto request = new CommentRequestDto("new comment");

        when(itemService.createComment(anyLong(), any(CommentRequestDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    String text = (invocationOnMock.getArgument(1, CommentRequestDto.class)).getText();
                    return CommentAnswerDto.builder()
                            .id(commentId)
                            .text(text)
                            .authorName("author name")
                            .created(LocalDateTime.now())
                            .build();
                });

        // act and assert
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, authorId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentId), Long.class))
                .andExpect(jsonPath("$.text", is(request.getText())));


        verify(itemService, times(1)).createComment(authorId, request, itemId);
    }

    @Test
    @SneakyThrows
    void getItem_itemExist_returnOk() {
        // arrange
        long itemId = 1L;
        long userId = 1L;
        ItemAnswerDto expected = Generator.makeItemAnswerDtoRequestDto();

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(expected);

        // act and assert
        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expected.getName())))
                .andExpect(jsonPath("$.description", is(expected.getDescription())))
                .andExpect(jsonPath("$.available", is(expected.getAvailable())));

        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @Test
    @SneakyThrows
    void getItems_itemExist_returnOkAndListOfDto() {
        // arrange
        long userId = 1L;
        ItemAnswerDto answerDto = Generator.makeItemAnswerDtoRequestDto();
        List<ItemAnswerDto> expected = Collections.singletonList(answerDto);

        when(itemService.getItemsByOwnerId(anyLong())).thenReturn(Collections.singletonList(answerDto));

        // act and assert
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expected.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(expected.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(expected.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(expected.get(0).getAvailable())));

        verify(itemService, times(1)).getItemsByOwnerId(userId);
    }

    @Test
    @SneakyThrows
    void patchItem_allPropertiesChange_returnOk() {
        // Arrange
        long ownerId = 1L;
        long itemId = 1L;
        Item item = Generator.makeItemWithoutOwner();
        item.setId(itemId);

        ItemRequestDto patch = ItemRequestDto.builder()
                .name("new item name")
                .description("new item description")
                .available(false)
                .build();

        ItemAnswerDto expectedResult = ItemAnswerDto.builder()
                .id(itemId)
                .name(patch.getName())
                .description(patch.getDescription())
                .available(patch.getAvailable())
                .build();

        when(itemService.updateItem(ownerId, patch, itemId)).thenReturn(expectedResult);

        // act and assert
        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, ownerId)
                        .content(mapper.writeValueAsString(patch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        verify(itemService, times(1)).updateItem(ownerId, patch, itemId);
    }

    @Test
    @SneakyThrows
    void searchItems_textProvided_returnOkAndListOfDto() {
        // Arrange
        long ownerId = 1L;
        long itemId = 1L;
        String searchText = "item name";
        Item item = Generator.makeItemWithoutOwner();
        item.setId(itemId);

        ItemAnswerDto itemAnswerDto = ItemAnswerDto.builder()
                .id(itemId)
                .name("item name")
                .description("description name")
                .available(true)
                .build();

        List<ItemAnswerDto> expectedResult = Collections.singletonList(itemAnswerDto);

        when(itemService.searchItems(searchText)).thenReturn(expectedResult);

        // act and assert
        mvc.perform(get("/items/search?text={searchText}", searchText)
                        .header(USER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        verify(itemService, times(1)).searchItems(searchText);
    }

}