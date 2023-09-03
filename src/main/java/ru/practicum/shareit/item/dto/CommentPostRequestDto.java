package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CommentPostRequestDto {
    @NotEmpty
    private String text;

    private Long itemId;
    private Long authorId;
}
