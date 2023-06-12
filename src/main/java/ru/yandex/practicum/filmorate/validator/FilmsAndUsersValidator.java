package ru.yandex.practicum.filmorate.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
public class FilmsAndUsersValidator {

    private static final Logger log = LoggerFactory.getLogger(FilmsAndUsersValidator.class);
    private final LocalDate releaseDateMax = LocalDate.of(1895, 12, 28);
    private int idFilms;
    private int idUsers;

    public Film validationFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма отсутствует.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(releaseDateMax)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getId() == 0) {
            film.setId(setIdFilm());
        }
        return film;
    }

    public User validationUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("У пользователя поля name отсутствует");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getId() == 0) {
            user.setId(setIdUser());
        }
        return user;
    }

    public int setIdUser() {
        return ++idUsers;
    }

    public int setIdFilm() {
        return ++idFilms;
    }
}
