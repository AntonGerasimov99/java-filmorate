package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void likedFilm(int filmId, int userId) {
        if (filmStorage.getFilmById(filmId).getLikesUsers().contains(userId)) {
            log.info("Вы уже поставили лайк этому фильму");
            throw new NotFoundElementException();
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikesUsers().add(userId);
        userStorage.getUserById(userId).getLikedFilms().add(filmId);
        film.setLikes(film.getLikes() + 1);
        log.info("Фильм добавлен в понравившиеся");
    }

    public void dislikedFilm(int userId, int filmId) {
        if (!filmStorage.getFilmById(filmId).getLikesUsers().contains(userId)) {
            log.info("В списке понравившихся фильм отсутствует");
            throw new NotFoundElementException();
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (!film.getLikesUsers().contains(userId)) {
            throw new NotFoundElementException();
        }
        film.getLikesUsers().remove(userId);
        userStorage.getUserById(userId).getLikedFilms().remove(filmId);
        film.setLikes(film.getLikes() - 1);
        log.info("Фильм убран из лайкнутых");
    }

    public List<Film> getPopularFilms(int limit) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findAll());
        return popularFilms.stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
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
}
