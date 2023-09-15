package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
public class AnswerDto {
    private Integer id;
    private String description;
    private User requester;
    private String created;
    private List<ItemShortAnswerDto> items;
}
