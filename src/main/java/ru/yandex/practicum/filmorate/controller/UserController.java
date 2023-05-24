package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id;

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        validation(user);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь под id: " + user.getId());
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        validation(user);
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь под id: " + user.getId() + " отсутствует");
            throw new ValidationException("Пользователь под id: " + user.getId() + " отсутствует");
        }
        users.put(user.getId(), user);
        log.info("Информация о пользователе под id: " + user.getId() + " обновлена");
        return user;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        ArrayList<User> res = new ArrayList<>();
        for (User value : users.values()) {
            res.add(value);
        }
        return res;
    }

    public User validation(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @." +
                    " Пользователь под id: " + user.getId());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым и содержать пробелы." + " Пользователь под id: " + user.getId());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("У пользователя поля name");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем." + " Пользователь под id: " + user.getId());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getId() == 0) {
            user.setId(setId());
        }
        return user;
    }

    public void removeUsers() {
        users.clear();
    }

    public int setId() {
        return ++id;
    }
}
