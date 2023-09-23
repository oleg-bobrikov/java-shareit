package ru.practicum.shareit.util;

import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;

public class Generator {
    public static UserShortDto makeUserShortDto() {
        return UserShortDto.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();
    }

    public static User makeUser() {
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

    public static ItemPostRequestDto makeItemPostRequestDto(){
        return ItemPostRequestDto.builder()

                .build();
    }
}
