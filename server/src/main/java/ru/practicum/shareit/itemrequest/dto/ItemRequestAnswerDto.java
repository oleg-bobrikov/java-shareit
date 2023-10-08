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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestAnswerDto {
    private Long id;
    private String description;
    private User requester;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    private List<ItemShortAnswerDto> items;
}
