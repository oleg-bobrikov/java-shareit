package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private ItemController controller;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    private MockMvc mvc;

    @Test
    void createItem_validDto_saveItemAndReturnStatusOk() {
    }
}