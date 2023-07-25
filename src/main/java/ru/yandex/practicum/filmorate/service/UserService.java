package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendsDbStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsDbStorage friendsDbStorage) {
        this.userStorage = userStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            log.info("Одинаковый пользователь");
            throw new NotFoundElementException();
        }
        friendsDbStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        friendsDbStorage.deleteFriend(userId, friendId);
    }

    public List<User> getMutualFriend(int userId, int friendId) {
        List<User> mutualFriends = new ArrayList<>();
        List<User> userFriends = friendsDbStorage.getFriendsById(userId);
        List<User> friendFriends = friendsDbStorage.getFriendsById(friendId);
        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            log.info("У одного из пользователей нет списка друзей");
            return mutualFriends;
        }
        Set<User> result = new HashSet<>(userFriends);
        result.retainAll(friendFriends);
        if (result.isEmpty()) {
            log.info("У пользователей нет общих друзей");
            return mutualFriends;
        } else {
            mutualFriends.addAll(result);
            return mutualFriends;
        }
    }

    public List<User> getFriendsById(int userId) {
        List<User> result = friendsDbStorage.getFriendsById(userId);
        return result;
    }

    public boolean isAlreadyFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        return ((user != null && user.getFriends().contains(friendId)) || (friend != null &&
                friend.getFriends().contains(friendId)));
    }

    public boolean isNotFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        return ((user != null && !user.getFriends().contains(friendId)) || (friend != null &&
                !friend.getFriends().contains(friendId)));
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User deleteUserById(int id) {
        return userStorage.deleteUserById(id);
    }
}