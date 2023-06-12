package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @NonNull
    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable int id, @PathVariable int userID) {
        return filmService.likedFilm(userID, id);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping(value = "/popular?count={count}")
    public ResponseEntity<List<Film>> getPopular(@PathVariable Optional<Integer> count) {
        List<Film> result = new ArrayList<>();
        if (count.isEmpty()) {
            return ResponseEntity.ok(filmService.getPopularFilms());
        } else {
            return ResponseEntity.ok(filmService.getPopularFilms(count.get()));
        }
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<String> dislike(@PathVariable int id, @PathVariable int userID) {
        return filmService.dislikedFilm(userID, id);
    }
}