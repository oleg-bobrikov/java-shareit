package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requester;
    private LocalDate created;
}
