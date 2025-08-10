package ru.practicum.mainsvc.user.service;

import ru.practicum.mainsvc.user.dto.NewUserDto;
import ru.practicum.mainsvc.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    Collection<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserDto userDto);

    void deleteUser(Long userId);
}
