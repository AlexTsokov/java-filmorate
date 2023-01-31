package ru.yandex.practicum.filmorate.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.Exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.Validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> allUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (!UserValidator.validate(user))
            throw new ValidationException("Ошибка валидации");
        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        else users.put(user.getEmail(), user);
        log.info("Добавлен пользователь " + user.getName());
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (!UserValidator.validate(user))
            throw new ValidationException("Ошибка валидации");
        else users.put(user.getEmail(), user);
        log.info("Обновлены данные пользователя " + user.getEmail());
        return user;
    }
}
