package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashSet;
import java.util.List;

@Repository
public class LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public List<Film> getPopularFilms(int limit) {
        String sql = "SELECT f.*, m.*, fl.user_id AS likes FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id ORDER BY COUNT(fl.film_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(List.of(rs.getInt("likes"))),
                        new MPA(rs.getInt("mpa_id"), rs.getString("mpa")),
                        new HashSet<>(genreDbStorage.getFilmGenres(rs.getInt("id")))),
                limit
        );
    }
}