package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.Instant;
import java.time.OffsetDateTime;

public class Generator {
    public static UserShortDto makeUserShortDto() {
        return UserShortDto.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();
    }

    public static User makeUser1() {
        return User.builder()
                .id(1L)
                .name("user1")
                .email("user1@gmail.com")
                .build();
    }

    public static UserDto makeUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@gmail.com")
                .build();
    }

    public static ItemPostRequestDto makeItemPostRequestDtoWithoutRequestId() {
        return ItemPostRequestDto.builder()
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();
    }

    public static ItemAnswerDto makeItemAnswerDtoRequestDto() {
        return ItemAnswerDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();
    }

    public static Item makeItemWithoutOwner() {
        return Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .isAvailable(true)
                .build();
    }

    public static Item makeItemWithOwner() {
        return Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .owner(makeUser1())
                .isAvailable(true)
                .build();
    }


    public static User makeUser2() {
        return User.builder()
                .id(2L)
                .name("user2")
                .email("user2@gmail.com")
                .build();
    }

    public static ItemRequest makeItemRequest1() {
        return ItemRequest.builder()
                .id(1L)
                .description("test item description")
                .createdDate(OffsetDateTime.now())
                .requester(makeUser1())
                .build();
    }

    public static ItemRequestAnswerDto makeItemRequestAnswerDto() {
        return ItemRequestAnswerDto.builder()
                .id(1L)
                .description("item request description")
                .created(InstantConverter.toPatternMillis(Instant.now()))
                .build();
    }

    public static ItemRequestRequestDto makeItemRequestRequestDto() {
        return ItemRequestRequestDto.builder()
                .description("item request description")
                .build();
    }

    public static BookingRequestDto makeBookingRequestDtoNextDay() {
        return BookingRequestDto.builder()
                .itemId(1L)
                .start(InstantConverter.toPattern(OffsetDateTime.now().plusDays(1).toInstant()))
                .end(InstantConverter.toPattern(OffsetDateTime.now().plusDays(2).toInstant()))
                .build();
    }

    public static Booking makeBooking() {
        return Booking.builder()
                .id(1L)
                .booker(Generator.makeUser2())
                .status(Status.WAITING)
                .startDate(OffsetDateTime.now().plusDays(1))
                .endDate(OffsetDateTime.now().plusDays(2))
                .item(Generator.makeItemWithOwner())
                .build();
    }

}
