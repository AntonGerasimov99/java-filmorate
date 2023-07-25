package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendDbTest {

    private final UserDbStorage userDbStorage;
    private final FriendsDbStorage friendsDbStorage;
    User user;
    User user2;

    @BeforeEach
    void setUp() {
        Set<Integer> friends = new HashSet<>();
        user = User.builder()
                .id(1)
                .email("abcd@mail.ru")
                .login("LoginName")
                .name("Name")
                .birthday(LocalDate.of(2000, 11, 15))
                .friends(friends)
                .build();

        Set<Integer> friends2 = new HashSet<>();
        user2 = User.builder()
                .id(2)
                .email("abcd2@mail.ru")
                .login("LoginName2")
                .name("Name2")
                .birthday(LocalDate.of(2001, 11, 15))
                .friends(friends2)
                .build();
    }

    @Test
    void addFriendsTest() {
        userDbStorage.create(user);
        userDbStorage.create(user2);
        friendsDbStorage.addFriend(user.getId(), user2.getId());
        List<User> test = friendsDbStorage.getFriendsById(user.getId());
        assertEquals(1, test.size());
    }
}