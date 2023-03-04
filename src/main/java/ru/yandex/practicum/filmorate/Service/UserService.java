package ru.yandex.practicum.filmorate.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    public final UserStorage inMemoryUserStorage;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        inMemoryUserStorage.getById(userId).getFriends().add(friendId);
        inMemoryUserStorage.getById(friendId).getFriends().add(userId);
        log.info("Новый друг добавлен");
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        inMemoryUserStorage.getById(userId).getFriends().remove(friendId);
        inMemoryUserStorage.getById(friendId).getFriends().remove(userId);
        log.info("Друг удален");
    }

    public List<User> getFriendsList(Integer userId) {
        checkExistUser(userId);
        List<User> userFriends = new ArrayList<>();
        for (Integer id : inMemoryUserStorage.getById(userId).getFriends()) {
            userFriends.add(inMemoryUserStorage.getById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriendsList(Integer userId, Integer otherId) {
        checkExistUser(userId);
        checkExistUser(otherId);
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userList = inMemoryUserStorage.getById(userId).getFriends();
        Set<Integer> otherList = inMemoryUserStorage.getById(otherId).getFriends();
        Set<Integer> commonListIds = userList.stream()
                .distinct()
                .filter(otherList::contains)
                .collect(Collectors.toSet());
        for (Integer id : commonListIds) {
            commonFriends.add(inMemoryUserStorage.getById(id));
        }
        return commonFriends;
    }

    public void checkExistUser(Integer userId) {
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
    }
}
