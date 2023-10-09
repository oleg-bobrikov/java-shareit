package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.shareit.common.Constant.DATE_TIME_PATTERN;

@Data
@Builder
public class CommentAnswerDto {
    private Long id;
    private String text;
    private String authorName;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
}
