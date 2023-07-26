package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendsDbStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userDbStorage;
    private final FriendsDbStorage friendsDbStorage;

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
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        return ((user != null && user.getFriends().contains(friendId)) || (friend != null &&
                friend.getFriends().contains(friendId)));
    }

    public boolean isNotFriend(int userId, int friendId) {
        User user = userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);
        return ((user != null && !user.getFriends().contains(friendId)) || (friend != null &&
                !friend.getFriends().contains(friendId)));
    }

    public User getUserById(int id) {
        return userDbStorage.getUserById(id);
    }

    public User create(User user) {
        return userDbStorage.create(user);
    }

    public User update(User user) {
        return userDbStorage.update(user);
    }

    public List<User> findAll() {
        return userDbStorage.findAll();
    }

    public User deleteUserById(int id) {
        return userDbStorage.deleteUserById(id);
    }
}