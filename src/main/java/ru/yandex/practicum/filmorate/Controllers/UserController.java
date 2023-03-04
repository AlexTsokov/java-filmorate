package ru.yandex.practicum.filmorate.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> allUsers() {
        return userService.inMemoryUserStorage.getAllUsersList();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userService.inMemoryUserStorage.addUser(user);
        return user;
    }

    @PutMapping
    public ResponseEntity put(@RequestBody User user) {
        userService.inMemoryUserStorage.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.inMemoryUserStorage.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return new ResponseEntity<>(userService.inMemoryUserStorage.getById(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable Integer id) {
        return new ResponseEntity<>(userService.getFriendsList(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return new ResponseEntity<>(userService.getCommonFriendsList(id, otherId), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<User>> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
        return new ResponseEntity<>(userService.getFriendsList(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<User>> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
        return new ResponseEntity<>(userService.getFriendsList(id), HttpStatus.OK);
    }
}
