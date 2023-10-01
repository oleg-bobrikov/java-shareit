package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.util.Generator;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    UserDto createTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .email("john.doe@mail.com")
                .name("John")
                .build();
    }

    UserShortDto createTestUserShortDto() {
        return UserShortDto.builder()
                .email("john.doe@mail.com")
                .name("John")
                .build();
    }

    @Test
    void shouldMapUserToDto() {

        assertNull(mapper.toDto((User) null));

        User user = Generator.makeUser1();

        assertThat(mapper.toDto(user))
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
    }


    @Test
    void shouldMapUserDtoToModel() {
        assertNull(mapper.toModel((UserDto) null));

        UserDto userDto = createTestUserDto();
        assertThat(mapper.toModel(userDto))
                .hasFieldOrPropertyWithValue("id", userDto.getId())
                .hasFieldOrPropertyWithValue("name", userDto.getName())
                .hasFieldOrPropertyWithValue("email", userDto.getEmail());
    }


    @Test
    void shouldMapShortUserDtoToModel() {
        assertNull(mapper.toModel((UserShortDto) null));

        UserShortDto userShortDto = createTestUserShortDto();
        assertThat(mapper.toModel(userShortDto))
                .hasFieldOrPropertyWithValue("name", userShortDto.getName())
                .hasFieldOrPropertyWithValue("email", userShortDto.getEmail());
    }

    @Test
    void shouldMapUserShortDtoToDto() {
        assertNull(mapper.toDto((UserShortDto) null));

        User user = Generator.makeUser1();
        assertThat(mapper.toDto(user))
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
    }

    @Test
    void shouldMapListOfUsersToDtoList() {
        assertNull(mapper.toDtoList(null));

        User user = Generator.makeUser1();
        assertThat(mapper.toDtoList(Collections.singletonList(user)))
                .asList()
                .hasSize(1)
                .satisfies(list -> assertThat(list.get(0))
                        .hasFieldOrPropertyWithValue("id", user.getId())
                        .hasFieldOrPropertyWithValue("name", user.getName())
                        .hasFieldOrPropertyWithValue("email", user.getEmail()));


    }

}