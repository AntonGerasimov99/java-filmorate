package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;
    private final LikeDbStorage likeDbStorage;

    public void likedFilm(int filmId, int userId) {
        checkFilmNotNull(filmId);
        checkUserNotNull(userId);
        likeDbStorage.addLike(filmId, userId);
        log.info("Фильм добавлен в понравившиеся");
    }

    public void dislikedFilm(int userId, int filmId) {
        checkFilmNotNull(filmId);
        checkUserNotNull(userId);
        likeDbStorage.deleteLike(filmId, userId);
        log.info("Фильм убран из лайкнутых");
    }

    public List<Film> getPopularFilms(int limit) {
        if (limit < 1) {
            throw new ValidationException("Лимит фильмов должен быть больше 0");
        }
        return likeDbStorage.getPopularFilms(limit);
    }

    public Film getFilmById(int id) {
        return filmDbStorage.getFilmById(id);
    }

    public Film update(Film film) {
        return filmDbStorage.update(film);
    }

    public Film create(Film film) {
        return filmDbStorage.create(film);
    }

    public List<Film> findAll() {
        return filmDbStorage.findAll();
    }

    public Film delete(int id) {
        return filmDbStorage.deleteFilmById(id);
    }

    public void checkFilmNotNull(int filmId) {
        Film film = filmDbStorage.getFilmById(filmId);
        if (film == null) {
            log.info("Фильм с данным id отсутствует");
            throw new NotFoundElementException();
        }
    }

    public void checkUserNotNull(int userId) {
        User user = userDbStorage.getUserById(userId);
        if (user == null) {
            log.info("Пользователь с данным id отсутствует");
            throw new NotFoundElementException();
        }
    }
}