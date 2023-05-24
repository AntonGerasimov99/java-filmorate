package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final LocalDate releaseDateMax = LocalDate.of(1895, 12, 28);
    private int id;

    @NonNull
    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        validation(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм под id: " + film.getId());
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        validation(film);
        if (!films.containsKey(film.getId())) {
            log.info("Фильм под id: " + film.getId() + " отсутствует");
            throw new ValidationException("Фильм под id: " + film.getId() + " отсутствует");
        }
        films.put(film.getId(), film);
        log.info("Информация о фильме под id: " + film.getId() + " обновлена");
        return film;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        ArrayList<Film> res = new ArrayList<>();
        for (Film value : films.values()) {
            res.add(value);
        }
        return res;
    }

    public Film validation(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Название фильма отсутствует." + " Фильм под id: " + film.getId());
            throw new ValidationException("Название фильма отсутствует.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания - 200 символов." + " Фильм под id: " + film.getId());
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(releaseDateMax)) {
            log.warn("Дата релиза — не раньше 28 декабря 1895 года." + " Фильм под id: " + film.getId());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration().isNegative()) {
            log.warn("Продолжительность фильма должна быть положительной." + " Фильм под id: " + film.getId());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getId() == 0) {
            film.setId(setId());
        }
        return film;
    }

    public void removeFilms() {
        films.clear();
    }

    public int setId() {
        return ++id;
    }
}
