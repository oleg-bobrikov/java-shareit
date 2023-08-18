package ru.practicum.shareit.booking;


import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;


@Data
public class Booking {
    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Item item;
    private User booker;
    private Status status;
}
