package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NoContentException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            log.info("Одинаковый пользователь");
            throw new NotFoundElementException();
        }
        if (isAlreadyFriend(userId, friendId)) {
            log.info("Пользователи уже друзья");
            throw new NoContentException();
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи добавлены в друзья");
    }

    public void deleteFriend(int userId, int friendId) {
        if (isNotFriend(userId, friendId)) {
            log.info("Пользователи не добавлены в друзья");
            throw new NoContentException();
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(userId);
        friend.getFriends().remove(userId);
    }

    public List<User> getMutualFriend(int userId, int friendId) {
        List<User> mutualFriends = new ArrayList<>();
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> friendFriends = userStorage.getUserById(friendId).getFriends();
        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            log.info("У одного из пользователей нет списка друзей");
            return mutualFriends;
        }
        Set<Integer> result = new HashSet<>(userFriends);
        result.retainAll(friendFriends);
        if (result.isEmpty()) {
            log.info("У пользователей нет общих друзей");
            return mutualFriends;
        } else {
            for (Integer integer : result) {
                mutualFriends.add(userStorage.getUserById(integer));
            }
            return mutualFriends;
        }
    }

    public List<User> getFriendsById(int userId) {
        Set<Integer> friends = userStorage.getUserById(userId).getFriends();
        List<User> result = new ArrayList<>();
        if (friends.isEmpty()) {
            throw new NotFoundElementException();
        }
        for (Integer friend : friends) {
            result.add(userStorage.getUserById(friend));
        }
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
        userStorage.create(user);
        return userStorage.getUserById(user.getId());
    }

    public User update(User user) {
        userStorage.update(user);
        return userStorage.getUserById(user.getId());
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }
}
