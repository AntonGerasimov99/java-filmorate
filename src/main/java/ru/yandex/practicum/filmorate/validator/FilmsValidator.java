package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmsValidator {

    private final LocalDate releaseDateMax = LocalDate.of(1895, 12, 28);
    private int idFilms;

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

    public int setIdFilm() {
        return ++idFilms;
    }
}