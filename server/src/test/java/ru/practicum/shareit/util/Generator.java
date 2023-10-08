package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.itemrequest.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.time.LocalDateTime;

public class Generator {
    public static UserRequestDto makeUserRequestDto1() {
        return new UserRequestDto("user1", "user1@gmail.com");
    }

    public static UserRequestDto makeUserRequestDto2() {
        return new UserRequestDto("user2", "user2@gmail.com");
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

    public static ItemRequestDto makeItemRequestDtoWithoutRequestId() {
        return ItemRequestDto.builder()
                .name("item name")
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
                .createdDate(LocalDateTime.now())
                .requester(makeUser1())
                .build();
    }

    public static ItemRequestAnswerDto makeItemRequestAnswerDto() {
        return ItemRequestAnswerDto.builder()
                .id(1L)
                .description("item request description")
                .created(LocalDateTime.now())
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
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    public static Booking makeBooking() {
        return Booking.builder()
                .id(1L)
                .booker(Generator.makeUser2())
                .status(Status.WAITING)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .item(Generator.makeItemWithOwner())
                .build();
    }

}
