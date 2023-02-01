package ru.yandex.practicum.filmorate.Validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    User user;

    void initUser() {
        user = new User(null,"w3ww5@ff.ru", "Login", "Alex",
                LocalDate.of(1985, 12, 22));
    }

    @Test
    void userEmailCantBeEmpty() {
        initUser();
        User user2 = new User(null,"", "Login", "Alex",
                LocalDate.of(1985, 12, 22));
        User user3 = new User(null," ", "Login", "Alex",
                LocalDate.of(1985, 12, 22));
        assertTrue(UserValidator.validate(user));
        assertFalse(UserValidator.validate(user2));
    }

    @Test
    void userLoginCantBeEmpty() {
        initUser();
        User user2 = new User(null,"w3ww5@ff.ru", "", "Alex",
                LocalDate.of(1985, 12, 22));
        User user3 = new User(null,"w3ww5@ff.ru", " ", "Alex",
                LocalDate.of(1985, 12, 22));
        assertTrue(UserValidator.validate(user));
        assertFalse(UserValidator.validate(user2));
        assertFalse(UserValidator.validate(user3));
    }

    @Test
    void userLoginCantHaveWhiteSpace() {
        initUser();
        User user2 = new User(null,"w3ww5@ff.ru", "Don Matron", "Alex",
                LocalDate.of(1985, 12, 22));
        assertTrue(UserValidator.validate(user));
        assertFalse(UserValidator.validate(user2));
    }


    @Test
    void userNameCanBeEmpty() {
        initUser();
        User user2 = new User(null,"w3ww5@ff.ru", "Login", "",
                LocalDate.of(1985, 12, 22));
        assertTrue(UserValidator.validate(user));
        assertTrue(UserValidator.validate(user2));
        assertEquals("Login", user2.getName());
    }

    @Test
    void userBirthDateCantBeInFuture() {
        initUser();
        User user2 = new User(null,"w3ww5@ff.ru", "Login", "",
                LocalDate.of(2028, 12, 22));
        assertTrue(UserValidator.validate(user));
        assertFalse(UserValidator.validate(user2));
    }
}