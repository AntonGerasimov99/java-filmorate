package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

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
    public ResponseEntity<String> addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.likedFilm(id, userId);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping(value = "{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(value = "/popular")
    public ResponseEntity<List<Film>> getPopular(@RequestParam Optional<Integer> count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count.orElse(10)));
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<String> dislike(@PathVariable int id, @PathVariable int userId) {
        return filmService.dislikedFilm(userId, id);
    }
}
