package ru.practicum.shareit.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemShortAnswerDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.common.Constant.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestAnswerDto {
    private Long id;
    private String description;
    private User requester;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    private List<ItemShortAnswerDto> items;
}
