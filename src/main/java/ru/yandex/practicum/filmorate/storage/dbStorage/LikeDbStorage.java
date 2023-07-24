package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashSet;
import java.util.List;

@Component
public class LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    public void addLike(int filmId, int userId){
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(int filmId, int userId){
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Integer> getLikes(int filmId){
        String sql = "SELECT user_id FROM films_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public List<Film> getPopularFilms(int limit){
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.id as mpa_id," +
                "m.mpa as mpa_name, g.name as genre_name, fg.genre_id, fl.user_id as likes " +
                "FROM films as f " +
                "LEFT JOIN film_likes as fl ON f.id = fl.film_id " +
                "LEFT JOIN mpa as m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres as fg ON fg.film_id = f.id " +
                "LEFT JOIN genres as g ON g.id = fg.genre_id " +
                "GROUP BY f.id ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(List.of(rs.getInt("likes"))),
                new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                new HashSet<>(genreDbStorage.getFilmGenres(rs.getInt("id")))),
                limit
        );
    }
}
