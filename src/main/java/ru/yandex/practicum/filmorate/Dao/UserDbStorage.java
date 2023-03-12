package ru.yandex.practicum.filmorate.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.Storage.UserStorage;
import ru.yandex.practicum.filmorate.Validators.UserValidator;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
@Qualifier
public class UserDbStorage implements UserStorage {
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        if (!UserValidator.validate(user)) throw new ValidationException("Ошибка валидации");
        final String sqlQuery = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        log.info("Пользователь создан" + user.getLogin());
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public List<User> getAllUsersList() {
        final String sqlQuery = "SELECT * FROM users";
        log.info("Список пользователей отправлен");
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User updateUser(User user) {
        if (!UserValidator.validate(user)) throw new ValidationException("Ошибка валидации");
        final String findQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet(findQuery, user.getId());
        if (!usersRows.next()) {
            log.info("Пользователь не найден", user.getId());
            throw new NotFoundException("Пользователь не найден.");
        }
        final String updateQuery = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ?" + "WHERE user_id = ?";
        jdbcTemplate.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь обновлен", user.getId());
        return user;
    }

    @Override
    public User getById(Integer id) {
        final String findUser = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(findUser, id);
        if (!sqlRowSet.next()) {
            log.info("Пользователь не найден ", id);
            throw new NotFoundException("Пользователь не найден");
        }
        final String getUser = "select * from users where user_id = ?";
        log.info("Пользователь отправлен", id);
        return jdbcTemplate.queryForObject(getUser, this::makeUser, id);
    }

    @Override
    public User deleteUser(Integer id) {
        final String deleteUser = "DELETE FROM users WHERE user_id = ?";
        User user = getById(id);
        jdbcTemplate.update(deleteUser, id);
        log.info("Пользователь удален", id);
        return user;
    }

    @Override
    public List<Integer> addFriendship(int firstId, int secondId) {
        checkIfExist(firstId, secondId);
        final String sqlForWriteQuery = "INSERT INTO friendship (friend_id, other_friend_id, status) " +
                "VALUES (?, ?, ?)";
        final String sqlForUpdateQuery = "UPDATE friendship SET status = ? " +
                "WHERE friend_id = ? AND other_friend_id = ?";
        final String checkMutualQuery = "SELECT * FROM friendship WHERE friend_id = ? AND other_friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkMutualQuery, firstId, secondId);

        if (userRows.first()) {
            jdbcTemplate.update(sqlForUpdateQuery, Friendship.APPROVED.toString(), firstId, secondId);
        } else {
            jdbcTemplate.update(sqlForWriteQuery, firstId, secondId, Friendship.DISAPPROVED.toString());
        }
        return List.of(firstId, secondId);
    }

    @Override
    public List<Integer> removeFriendship(int firstId, int secondId) {
        final String sqlQuery = "DELETE FROM friendship WHERE friend_id = ? and other_friend_id = ?";
        jdbcTemplate.update(sqlQuery, firstId, secondId);
        return List.of(firstId, secondId);
    }

    @Override
    public List<User> getFriendsListById(int id) {
        final String checkQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet followingRow = jdbcTemplate.queryForRowSet(checkQuery, id);
        if (!followingRow.next()) {
            log.warn("Пользователь не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }

        final String sqlQuery = "SELECT user_id, email, login, name, birthday " +
                "FROM USERS " +
                "LEFT JOIN friendship mf on users.user_id = mf.other_friend_id " +
                "where friend_id = ? AND status='DISAPPROVED'";

        log.info("Список друзей пользователя отправлен", id);
        return jdbcTemplate.query(sqlQuery, this::makeUser, id);
    }

    @Override
    public List<User> getSharedFriendsList(int firstId, int secondId) {
        checkIfExist(firstId, secondId);
        final String sqlQuery = "SELECT user_id, email, login, name, birthday " +
                "FROM friendship AS mf " +
                "LEFT JOIN users u ON u.user_id = mf.other_friend_id " +
                "WHERE mf.friend_id = ? AND mf.other_friend_id IN ( " +
                "SELECT other_friend_id " +
                "FROM friendship AS mf " +
                "LEFT JOIN users AS u ON u.user_id = mf.other_friend_id " +
                "WHERE mf.friend_id = ? )";
        log.info("Список общих друзей отправлен", firstId, secondId);
        return jdbcTemplate.query(sqlQuery, this::makeUser, firstId, secondId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    private void checkIfExist(int firstId, int secondId) {
        final String check = "SELECT 1 where exists(select * FROM users WHERE user_id=?) " +
                "and exists(select * FROM users WHERE user_id=?)";
        SqlRowSet followingRow = jdbcTemplate.queryForRowSet(check, firstId, secondId);
        if (!followingRow.next()) {
            log.warn("Пользователи не найдены", firstId, secondId);
            throw new NotFoundException("Пользователи не найдены");
        }
    }
}
