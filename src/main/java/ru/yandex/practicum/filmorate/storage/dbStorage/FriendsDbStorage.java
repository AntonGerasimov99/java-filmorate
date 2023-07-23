package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
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
        boolean statusFriends = false;
        if (user == null || friend == null) {
            throw new NotFoundElementException();
        }
        if (friend.getFriends().contains(userId)) {
            statusFriends = true;
            String sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId, statusFriends, userId, friendId);
        } else {
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?,?,?)";
            jdbcTemplate.update(sql, userId, statusFriends);
        }
    }

    public void deleteFriend(int userId, int friendId){
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundElementException();
        }
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId);
        if (friend.getFriends().contains(userId)){
            String sqlStatus = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId, false, userId, friendId);
        }
    }

    public List<User> getFriendsById(int id){
        User user = userDbStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundElementException();
        }
        String sql = "SELECT friend_id, email, login, name, birthday FROM friends as f" +
                "INNER JOIN users as u ON f.friend_id = u.id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("friend_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null
        ),id);
    }
}
