package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("Get user by id={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get users");
        return userClient.getUsers();
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserRequestDto requestDto) {
        log.info("Creating user {}", requestDto);
        return userClient.createUser(requestDto);
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserRequestDto requestDto, @PathVariable long userId) {
        log.info("Updating user {} by userId={}", requestDto, userId);
        return userClient.updateUser(userId, requestDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userClient.deleteUserById(userId);
    }
}
