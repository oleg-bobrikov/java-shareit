package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping()
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto){
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        userDto.setId(userId);
        return userService.patchUser(userDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }
}
