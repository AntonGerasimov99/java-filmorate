package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.inMemoryStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userService);
    User user;
    User userTest;
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .email("abcd@mail.ru")
                .login("LoginName")
                .name("Name")
                .birthday(LocalDate.of(2000, 11, 15))
                .build();
    }

    @AfterEach
    void tearDown() {
        inMemoryUserStorage.removeUsers();
    }

    @Test
    void shouldCreateUser() {
        userController.create(user);
        assertNotNull(userController.findAll());
    }

    @Test
    void shouldNotCreateWithEmptyLogin() {
        user.setLogin(" ");
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldCreateWithEmptyName() {
        userTest = User.builder()
                .id(1)
                .email("abcd2@mail.ru")
                .login("LoginName2")
                .name("")
                .birthday(LocalDate.of(2000, 11, 15))
                .build();
        userController.create(userTest);
        assertNotNull(userController.findAll());
    }

    @Test
    void shouldNotCreateWithSpaceLogin() {
        user.setLogin("Login Name");
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotCreateEmailWithoutA() {
        user.setEmail("mail");
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateEmptyEmail() {
        user.setEmail("mail");
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateWithWrongDateOfBirthday() {
        user.setBirthday(LocalDate.of(2050, 10, 12));
        Throwable exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldUpdateUser() {
        userController.create(user);
        assertNotNull(userController.findAll());
        userTest = User.builder()
                .id(1)
                .email("abcd2@mail.ru")
                .login("LoginName2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 11, 15))
                .build();
        userController.update(userTest);
        assertEquals("Name2", userController.findAll().get(0).getName());
    }

    @Test
    void shouldFindAllUser() {
        userController.create(user);
        assertNotNull(userController.findAll());
        userTest = User.builder()
                .id(2)
                .email("abcd2@mail.ru")
                .login("LoginName2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 11, 15))
                .build();
        userController.create(userTest);
        assertEquals(2, userController.findAll().size());
    }
}
