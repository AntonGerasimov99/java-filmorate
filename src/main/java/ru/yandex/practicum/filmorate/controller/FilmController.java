package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.likedFilm(id, userId);
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
    public List<Film> getPopular(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void dislike(@PathVariable int id, @PathVariable int userId) {
        filmService.dislikedFilm(id, userId);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable int id) {
        filmService.delete(id);
    }
}