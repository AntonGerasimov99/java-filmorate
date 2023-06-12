package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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

    public ResponseEntity<String> likedFilm(int userId, int filmId) {
        if (filmStorage.findAll().get(filmId).getLikesUsers().contains(userId)) {
            return new ResponseEntity<>("Вы уже поставили лайк этому фильму", HttpStatus.ALREADY_REPORTED);
        }
        Film film = filmStorage.findAll().get(filmId);
        film.getLikesUsers().add(userId);
        userStorage.getUserById(userId).getLikedFilms().add(filmId);
        film.setLikes(film.getLikes() + 1);
        log.info("Фильм добавлен в понравившиеся");
        return new ResponseEntity<>("Вы лайкнули фильм", HttpStatus.OK);
    }

    public ResponseEntity<String> dislikedFilm(int userId, int filmId) {
        if (!filmStorage.findAll().get(filmId).getLikesUsers().contains(userId)) {
            return new ResponseEntity<>("В списке понравившихся фильм отсутствует", HttpStatus.ALREADY_REPORTED);
        }
        Film film = filmStorage.findAll().get(filmId);
        film.getLikesUsers().remove(userId);
        userStorage.getUserById(userId).getLikedFilms().remove(filmId);
        film.setLikes(film.getLikes() - 1);
        log.info("Фильм убран из лайкнутых");
        return new ResponseEntity<>("Фильм убран из лайкнутых", HttpStatus.OK);
    }

    public List<Film> getPopularFilms() {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findAll());
        return popularFilms.stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Film> getPopularFilms(int limit) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findAll());
        return popularFilms.stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
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
