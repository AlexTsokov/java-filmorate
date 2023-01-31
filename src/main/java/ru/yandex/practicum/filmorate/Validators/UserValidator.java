package ru.yandex.practicum.filmorate.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

public class UserValidator {
    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);

    public static boolean validate(User user) {
        if (user.getName().isBlank())
            user.setName(user.getLogin());
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            log.error("Поле почты не заполнено");
            return false;
        } else if (user.getLogin().isBlank()) {
            log.error("Логин не заполнен");
            return false;
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения в будущем");
            return false;
        } else return true;
    }

    public static void noFoundUser(User user, Map<Integer, User> users) {
        if (!users.containsKey(user.getId())) {
            log.info("Проверка, существует ли пользователь");
            throw new ValidationException("Такого пользователя нет");
        }
    }
}
