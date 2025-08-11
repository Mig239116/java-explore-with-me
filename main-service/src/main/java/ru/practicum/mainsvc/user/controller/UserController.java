package ru.practicum.mainsvc.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import ru.practicum.mainsvc.user.dto.NewUserDto;
import ru.practicum.mainsvc.user.dto.UserDto;
import ru.practicum.mainsvc.user.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        if (ids == null || ids.isEmpty()) {
            log.debug("GET/admin/users/: returning all users ");
        } else {
            log.debug("GET/admin/users/: returning users with ids {}", ids);
        }
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserDto userDto) {
        log.debug("POST/users - adding new user {} with email {}",
                userDto.getName(),
                userDto.getEmail()
        );
        UserDto userDtoStored = userService.addUser(userDto);
        log.debug("POST/user: the process was completed successfully. A new user {} with id {} has been created",
                userDtoStored.getName(),
                userDtoStored.getId()
        );
        return userDtoStored;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.debug("DELETE/users/id: deleting user {}", userId);
        userService.deleteUser(userId);
    }

}
