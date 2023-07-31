package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Repository
public class MPADbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MPA getMPAById(Integer id) {
        MPA mpa;
        SqlRowSet findMPA = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?", id);
        if (findMPA.first()) {
            mpa = new MPA(
                    findMPA.getInt("id"),
                    findMPA.getString("mpa")
            );
        } else {
            throw new NotFoundElementException();
        }
        return mpa;
    }

    public List<MPA> findAllMPA() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MPA(
                rs.getInt("id"),
                rs.getString("mpa")
        ));
    }
}