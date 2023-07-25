package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {

    private final UserDbStorage userDbStorage;
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
    void getUserById() {
        userDbStorage.create(user);
        User test = userDbStorage.getUserById(1);
        assertThat(test).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void findAllUsersTest() {
        userDbStorage.create(user2);
        List<User> test = userDbStorage.findAll();
        assertEquals(2, test.size());
    }

    @Test
    void updateUserTest() {
        userDbStorage.create(user);
        user.setName("newName");
        userDbStorage.update(user);
        User test = userDbStorage.getUserById(1);
        assertThat(test).hasFieldOrPropertyWithValue("name", "newName");
    }

    @Test
    void deleteUserTest() {
        userDbStorage.create(user);
        userDbStorage.create(user2);
        List<User> test = userDbStorage.findAll();
        userDbStorage.deleteUserById(1);
        List<User> test2 = userDbStorage.findAll();
        assertEquals(test.size() - 1, test2.size());
    }
}