package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDBTest {

    private final FilmDbStorage filmDbStorage;
    Film film;
    Film film2;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .id(1)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 11, 15))
                .duration(120)
                .likesUsers(new HashSet<>())
                .mpa(new MPA(3, "PG-13"))
                .genres(new HashSet<>())
                .build();

        film2 = Film.builder()
                .id(2)
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(2002, 11, 15))
                .duration(130)
                .likesUsers(new HashSet<>())
                .mpa(new MPA(3, "PG-13"))
                .genres(new HashSet<>())
                .build();
    }

    @Test
    void getFilmByIdTest() {
        filmDbStorage.create(film);
        Film test = filmDbStorage.getFilmById(1);
        assertThat(test).hasFieldOrPropertyWithValue("id", 1);
        filmDbStorage.deleteFilmById(film.getId());
    }

    @Test
    void updateUserTest() {
        filmDbStorage.create(film);
        film.setName("newName");
        int id = film.getId();
        filmDbStorage.update(film);
        Film test = filmDbStorage.getFilmById(id);
        assertThat(test).hasFieldOrPropertyWithValue("name", "newName");
        filmDbStorage.deleteFilmById(film.getId());
    }

    @Test
    void deleteFilmTest() {
        filmDbStorage.create(film);
        int id = film.getId();
        filmDbStorage.create(film2);
        List<Film> test = filmDbStorage.findAll();
        filmDbStorage.deleteFilmById(id);
        List<Film> test2 = filmDbStorage.findAll();
        assertEquals(test.size() - 1, test2.size());
        filmDbStorage.deleteFilmById(film2.getId());
    }
}