package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NoContentException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class FriendsDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        boolean status = false;
        if (user == null || friend == null) {
            log.info("Один из пользователей не найден");
            throw new NotFoundElementException();
        }
        if (userId == friendId) {
            log.info("Одинаковый пользователь");
            throw new NotFoundElementException();
        }
        if (findIdFriend(friendId).contains(userId)) {
            status = true;
            String sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, friendId, userId, status, userId, friendId);
        } else {
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
            jdbcTemplate.update(sql, userId, friendId, status);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        if (user == null || friend == null) {
            log.info("Один из пользователей не найден");
            throw new NotFoundElementException();
        }
        if (userId == friendId) {
            log.info("Одинаковый пользователь");
            throw new NotFoundElementException();
        }
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        if (getFriendsById(friendId).contains(user)) {
            String sqlStatus = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlStatus, userId, friendId, false, userId, friendId);
        } else {
            log.info("Пользователи не добавлены в друзья");
            throw new NoContentException();
        }
    }

    public List<User> getFriendsById(int id) {
        User user = userDbStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundElementException();
        }
        String sql = "SELECT friend_id, email, login, name, birthday FROM friends as f " +
                "INNER JOIN users as u ON f.friend_id = u.id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("friend_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null
        ), id);
    }

    public Set<Integer> findIdFriend(int userId) {
        User user = userDbStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundElementException();
        }
        Set<Integer> friends = new HashSet<>();
        String query = "SELECT friend_id FROM friends WHERE user_id = ?";
        jdbcTemplate.query(query, new Object[]{userId}, (rs, rowNum) -> {
            friends.add(rs.getInt("friend_id"));
            return friends;
        });
        return friends;
    }
}