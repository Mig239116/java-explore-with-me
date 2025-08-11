package ru.practicum.mainsvc.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainsvc.errors.ConflictException;
import ru.practicum.mainsvc.errors.NotFoundException;
import ru.practicum.mainsvc.user.dto.NewUserDto;
import ru.practicum.mainsvc.user.dto.UserDto;
import ru.practicum.mainsvc.user.mapper.UserMapper;
import ru.practicum.mainsvc.user.model.User;
import ru.practicum.mainsvc.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Collection<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, page).getContent();
        }
        return users.stream()
                .map(UserMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserDto newUserDto) {
        validateEmail(newUserDto.getEmail());
        User user = UserMapper.toUser(newUserDto);
        return UserMapper.toFullDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        validateNotFound(userId);
        userRepository.deleteById(userId);
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Text to be checked");
        }
    }

    private User validateNotFound(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("User " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }
}
