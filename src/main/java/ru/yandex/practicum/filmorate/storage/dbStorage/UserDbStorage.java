package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        validationUser(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users(email, login, name, birthday)" + " VALUES(?,?,?,?)";
        jdbcTemplate.update(s -> {
            PreparedStatement statement = s.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        return user;
    }

    @Override
    public User update(User user) {
        if (getUserById(user.getId()) == null) {
            throw new NotFoundElementException();
        }
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        validationUser(user);
        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null,
                null
        )));
    }

    @Override
    public User getUserById(int id) {
        User user;
        SqlRowSet findUser = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id =?", id);
        if (findUser.first()) {
            user = new User(
                    findUser.getInt("id"),
                    findUser.getString("email"),
                    findUser.getString("login"),
                    findUser.getString("name"),
                    findUser.getDate("birthday").toLocalDate(),
                    null,
                    null
            );
        } else {
            throw new NotFoundElementException();
        }
        return user;
    }

    public User deleteUserById(int id) {
        User user = getUserById(id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, user.getId());
        return user;
    }

    public User validationUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("У пользователя поля name отсутствует");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return user;
    }
}