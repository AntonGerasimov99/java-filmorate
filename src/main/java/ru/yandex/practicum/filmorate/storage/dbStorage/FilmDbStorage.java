package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private MPADbStorage mpaDbStorage;
    private LikeDbStorage likeDbStorage;
    private GenreDbStorage genreDbStorage;
    private int idFilms;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MPADbStorage mpaDbStorage, LikeDbStorage likeDbStorage,
                         GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film create(Film film) {
        validationFilm(film);
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id)" + " VALUES(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(s -> {
            PreparedStatement statement = s.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(mpaDbStorage.getMPAById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreDbStorage.getGenreById(genre.getId()).getName());
            }
            genreDbStorage.deleteFilm(film); // todo объединить??
            genreDbStorage.addFilm(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        validationFilm(film);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE id = ?";
        if (jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) != 0) {
            film.setMpa(mpaDbStorage.getMPAById(film.getMpa().getId()));
            if (film.getGenres() != null) {
                Collection<Genre> sortedGenres = film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toList());
                film.setGenres(new LinkedHashSet<>(sortedGenres));
                for (Genre genre : film.getGenres()) {
                    genre.setName(genreDbStorage.getGenreById(genre.getId()).getName());
                }
            }
            genreDbStorage.deleteFilm(film); // todo объединить??
            genreDbStorage.addFilm(film);
            return film;
        } else {
            throw new NotFoundElementException();
        }
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "fl.user_id, fl.film_id FROM films as f LEFT JOIN film_likes as fl " +
                "ON f.id = fl.film_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(List.of(rs.getInt("user_id"))),
                mpaDbStorage.getMPAById(rs.getInt("mpa_id")),
                new HashSet<>(genreDbStorage.getFilmGenres(rs.getInt("id")))
        ));
    }

    @Override
    public void removeFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    public Film deleteFilmById(int id) {
        Film film = getFilmById(id);
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film;
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "fl.user_id, fl.film_id FROM films as f LEFT JOIN film_likes as fl " +
                "ON f.id = fl.film_id " +
                "WHERE id = ?";
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRowSet.first()) {
            film = new Film(
                    filmRowSet.getInt("id"),
                    filmRowSet.getString("name"),
                    filmRowSet.getString("description"),
                    filmRowSet.getDate("release_date").toLocalDate(),
                    filmRowSet.getInt("duration"),
                    new HashSet<>(List.of(filmRowSet.getInt("user_id"))),
                    mpaDbStorage.getMPAById(filmRowSet.getInt("mpa_id")),
                    new HashSet<>(genreDbStorage.getFilmGenres(id))
            );
        } else {
            throw new NotFoundElementException();
        }
        return film;
    }

    public Film validationFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма отсутствует.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return film;
    }

    public int setIdFilm() {
        return ++idFilms;
    }
}