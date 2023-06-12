package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validator.FilmsAndUsersValidator;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final HashMap<Integer, Film> films = new HashMap<>();
    FilmsAndUsersValidator filmsAndUsersValidator = new FilmsAndUsersValidator();

    @Override
    public Film create(Film film) {
        filmsAndUsersValidator.validationFilm(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм под id: " + film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        filmsAndUsersValidator.validationFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм под id: " + film.getId() + " отсутствует");
        }
        films.put(film.getId(), film);
        log.info("Информация о фильме под id: " + film.getId() + " обновлена");
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        ArrayList<Film> res = new ArrayList<>();
        for (Film value : films.values()) {
            res.add(value);
        }
        return res;
    }

    @Override
    public void removeFilms() {
        films.clear();
    }
}
