package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.LikeDbStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikeDbStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public void likedFilm(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null) {
            log.info("Фильм с данным id отсутствует");
            throw new NotFoundElementException();
        }
        if (user == null) {
            log.info("Пользователь с данным id отсутствует");
            throw new NotFoundElementException();
        }
        likeStorage.addLike(filmId, userId);
        log.info("Фильм добавлен в понравившиеся");
    }

    public void dislikedFilm(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null) {
            log.info("Фильм с данным id отсутствует");
            throw new NotFoundElementException();
        }
        if (user == null) {
            log.info("Пользователь с данным id отсутствует");
            throw new NotFoundElementException();
        }
        likeStorage.deleteLike(filmId, userId);
        log.info("Фильм убран из лайкнутых");
    }

    public List<Film> getPopularFilms(int limit) {
        if (limit < 1) {
            throw new ValidationException("Лимит фильмов должен быть больше 0");
        }
        return likeStorage.getPopularFilms(limit);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film delete(int id) {
        return filmStorage.deleteFilmById(id);
    }
}