package ru.yandex.practicum.filmorate.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Controllers.UserController;
import ru.yandex.practicum.filmorate.Exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.Validators.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Integer userId = 0;

    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if (!UserValidator.validate(user))
            throw new ValidationException("Ошибка валидации");
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        } else {
            Integer id = generatedId();
            user.setId(id);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь " + user.getName());
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Такого пользователя нет");
        }
        UserValidator.checkIfUserExists(user, users);
        users.put(user.getId(), user);
        log.info("Успешное изменение пользователя");
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public Integer generatedId() {
        userId++;
        return userId;
    }

    @Override
    public List<User> getAllUsersList() {
        return new ArrayList<>(users.values());
    }

}
