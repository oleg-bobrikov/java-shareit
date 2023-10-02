package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemAnswerDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    @Getter(AccessLevel.NONE)
    private List<CommentAnswerDto> comments;

    public List<CommentAnswerDto> getComments() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    private Long requestId;
}
