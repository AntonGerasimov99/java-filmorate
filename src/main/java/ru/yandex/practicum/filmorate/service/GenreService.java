package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.GenreDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> getGenres() {
        return genreDbStorage.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public void addFilm(Film film) {
        genreDbStorage.addFilm(film);
    }

    public Genre getGenreById(int id) {
        return genreDbStorage.getGenreById(id);
    }

    public void deleteFilm(Film film) {
        genreDbStorage.deleteFilm(film);
    }

    public List<Genre> getFilmGenres(int id) {
        return genreDbStorage.getFilmGenres(id);
    }
}