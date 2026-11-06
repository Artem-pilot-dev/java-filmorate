package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.dto.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        User user = UserMapper.toUser(dto);
        validateUser(user);
        User savedUser = userStorage.createUser(user);
        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto updateUser(UserRequestDto dto) {
        User user = UserMapper.toUser(dto);
        validateUser(user);
        User updatedUser = userStorage.updateUser(user);
        return UserMapper.toResponseDto(updatedUser);
    }

    private User getStoredUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        return UserMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> listOfUsers = userStorage.getAllUsers();
        return listOfUsers.stream().map(user -> UserMapper.toResponseDto(user)).toList();
    }

    public UserResponseDto addFriend(Long userId, Long friendId) {
        User user = getStoredUserById(userId);
        User friend = getStoredUserById(friendId);
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return UserMapper.toResponseDto(user);
    }

    public UserResponseDto removeFriend(Long userId, Long friendId) {
        User user = getStoredUserById(userId);
        User friend = getStoredUserById(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        return UserMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getFriends(Long userId) {
        return getStoredUserById(userId).getFriends().stream()
                .map(id -> UserMapper.toResponseDto(getStoredUserById(id)))
                .toList();
    }

    public List<UserResponseDto> getCommonFriends(Long userId, Long otherId) {
        User user = getStoredUserById(userId);
        User otherUser = getStoredUserById(otherId);
        return user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .map(id -> UserMapper.toResponseDto(getStoredUserById(id)))
                .toList();
    }


    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин пользователя с id {} пустой или содержит пробелы", user.getId());
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
