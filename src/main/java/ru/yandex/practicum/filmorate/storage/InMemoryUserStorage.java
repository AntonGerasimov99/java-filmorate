package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UsersValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    UsersValidator usersValidator = new UsersValidator();
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        usersValidator.validationUser(user);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь под id: " + user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        usersValidator.validationUser(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь под id: " + user.getId() + " отсутствует");
        }
        users.put(user.getId(), user);
        log.info("Информация о пользователе под id: " + user.getId() + " обновлена");
        return user;
    }

    @Override
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        ArrayList<User> res = new ArrayList<>();
        for (User value : users.values()) {
            res.add(value);
        }
        return res;
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundElementException();
        }
        return users.get(id);
    }

    public void removeUsers() {
        users.clear();
    }
}
