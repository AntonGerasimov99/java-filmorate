package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    FilmController filmController = new FilmController();
    Film film;
    Film filmTest;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .id(1)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 11, 15))
                .duration(120)
                .build();
    }

    @AfterEach
    void tearDown() {
        filmController.removeFilms();
    }

    @Test
    void shouldCreateFilmTest() {
        filmController.create(film);
        assertNotNull(filmController.findAll());
        System.out.println(film.getDuration());
    }

    @Test
    void shouldNotCreateFilmWithEmptyNameTest() {
        film.setName(" ");
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Название фильма отсутствует.", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithBigDescriptionTest() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Максимальная длина описания - 200 символов", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithOldDateTest() {
        film.setReleaseDate(LocalDate.of(1800, 10, 10));
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDurationTest() {
        film.setDuration(-200);
        Throwable exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldAllFindsFilmTest() {
        filmController.create(film);
        filmTest = Film.builder()
                .id(2)
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 11, 15))
                .duration(120)
                .build();
        filmController.create(filmTest);
        assertEquals(2, filmController.findAll().size());
    }

    @Test
    void shouldUpdateFilmTest() {
        filmController.create(film);
        filmTest = Film.builder()
                .id(1)
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 11, 15))
                .duration(120)
                .build();
        filmController.create(filmTest);
        assertEquals("Name2", filmController.findAll().get(0).getName());
    }
}
