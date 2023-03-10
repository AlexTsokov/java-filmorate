package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    List<User> getAllUsersList();

    User updateUser(User user);

    User getById(Integer id);

    User deleteUser(Integer id);

    List<Integer> addFriendship(int firstId, int secondId);

    List<Integer> removeFriendship(int firstId, int secondId);

    List<User> getFriendsListById(int id);

    List<User> getSharedFriendsList(int firstId, int secondId);

}