package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

    List<User> getAllUsersList();

    User getById(Integer id);
}