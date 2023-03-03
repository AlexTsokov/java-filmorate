package ru.yandex.practicum.filmorate.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Pattern;

public class UserValidator {
    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);
    private static final String emailPattern = "^(.+)@(\\S+)$";
    private static final Pattern loginPattern = Pattern.compile("\\s");

    public static boolean validate(User user) {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Поле почты не заполнено");
            return false;
        } else if (!patternMatches(user.getEmail(), emailPattern)) {
            log.error("Поле почты не корректно");
            return false;
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().isBlank()) {
            log.error("Логин не заполнен");
            return false;
        } else if (loginPattern.matcher(user.getLogin()).find()) {
            log.error("Логин может быть с пробелом");
            return false;
        } else if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения некорректна");
            return false;
        } else return true;
    }

    public static void checkIfUserExists(User user, Map<Integer, User> users) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.info("Проверка, существует ли пользователь");
            throw new ValidationException("Такого пользователя нет");
        }
    }

    public static boolean patternMatches(String emailAddress, String emailPattern) {
        return Pattern.compile(emailPattern)
                .matcher(emailAddress)
                .matches();
    }
}
