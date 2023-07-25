package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbTest {

    private final LikeDbStorage likeDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    Film film;
    Film film2;
    User user;
    User user2;

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

        Set<Integer> friends = new HashSet<>();
        user = User.builder()
                .id(1)
                .email("abcd@mail.ru")
                .login("LoginName")
                .name("Name")
                .birthday(LocalDate.of(2000, 11, 15))
                .friends(friends)
                .build();

        Set<Integer> friends2 = new HashSet<>();
        user2 = User.builder()
                .id(2)
                .email("abcd2@mail.ru")
                .login("LoginName2")
                .name("Name2")
                .birthday(LocalDate.of(2001, 11, 15))
                .friends(friends2)
                .build();

        filmDbStorage.create(film);
        filmDbStorage.create(film2);
        userDbStorage.create(user);
        userDbStorage.create(user2);
    }

    @Test
    void addLikeToFilmTest() {
        likeDbStorage.addLike(film.getId(), user.getId());
        List<Integer> likes = likeDbStorage.getLikes(film.getId());
        assertEquals(1, likes.size());
        likeDbStorage.deleteLike(film.getId(), user.getId());
    }

    @Test
    void addAndDeleteLikeToFilmTest() {
        likeDbStorage.addLike(film.getId(), user.getId());
        List<Integer> likes = likeDbStorage.getLikes(film.getId());
        likeDbStorage.deleteLike(film.getId(), user.getId());
        List<Integer> test = likeDbStorage.getLikes(film.getId());
        assertEquals(likes.size() - 1, test.size());
    }

    @Test
    void getOnePopularFilm() {
        likeDbStorage.addLike(film.getId(), user.getId());
        likeDbStorage.addLike(film2.getId(), user.getId());
        List<Film> test = likeDbStorage.getPopularFilms(1);
        assertEquals(1, test.size());
        likeDbStorage.deleteLike(film.getId(), user.getId());
        likeDbStorage.deleteLike(film2.getId(), user.getId());
    }

    @Test
    void getTwoPopularFilm() {
        likeDbStorage.addLike(film.getId(), user.getId());
        likeDbStorage.addLike(film2.getId(), user.getId());
        List<Film> test = likeDbStorage.getPopularFilms(2);
        assertEquals(2, test.size());
        likeDbStorage.deleteLike(film.getId(), user.getId());
        likeDbStorage.deleteLike(film2.getId(), user.getId());
    }
}