package ru.practicum.shareit.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestAnswerDto {
    private Long id;
    private String description;
    private User requester;
    private String created;
    private List<ItemShortAnswerDto> items;
}
