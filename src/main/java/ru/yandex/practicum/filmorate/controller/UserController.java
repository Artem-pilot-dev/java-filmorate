package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь с id {}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Не указан id у пользователя {}", user.getLogin());
            throw new ValidationException("id не указан");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Отсутствует пользователь с id = {}", user.getId());
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Данные пользователя с id {} обновлены", user.getId());
        return user;
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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
