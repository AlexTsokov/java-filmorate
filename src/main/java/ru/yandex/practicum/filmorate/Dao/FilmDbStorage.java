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
import ru.yandex.practicum.filmorate.Storage.FilmStorage;
import ru.yandex.practicum.filmorate.Validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@Qualifier
public class FilmDbStorage implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        if (!FilmValidator.validate(film)) throw new ValidationException("Ошибка валидации");
        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            return statement;
        }, generatedId);
        film.setId(Objects.requireNonNull(generatedId.getKey()).intValue());
        final String mpaSqlQuery = "INSERT INTO films_mpa (films_id, rating_mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(mpaSqlQuery, film.getId(), film.getMpa().getId());
        final String genresSqlQuery = "INSERT INTO film_genre (film_id, genres_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genresSqlQuery, film.getId(), g.getId());
            }
        }
        return film;
    }

    @Override
    public List<Film> getAllFilmsList() {
        final String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!FilmValidator.validate(film)) throw new ValidationException("Ошибка валидации");
        final String findQuery = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(findQuery, film.getId());
        if (!sqlRowSet.next()) {
            log.info("Фильм не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        final String updateQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?" +
                "WHERE film_id = ?";
        if (film.getMpa() != null) {
            final String updateMpa = "UPDATE films_mpa SET rating_mpa_id = ? WHERE films_id = ?";
            jdbcTemplate.update(updateMpa, film.getMpa().getId(), film.getId());
        }
        if (film.getGenres() != null) {
            final String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
            final String updateGenresQuery = "INSERT INTO film_genre (film_id, genres_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteGenresQuery, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genres_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }
        jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        log.info("Фильм обновлен ", film.getId());
        return film;
    }

    @Override
    public Film getById(Integer id) {
        final String findFilm = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(findFilm, id);
        if (!sqlRowSet.next()) {
            log.info("Фильм не найден ", id);
            throw new NotFoundException("Фильм не найден");
        }
        final String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
    }

    @Override
    public Film deleteFilm(Integer id) {
        final String findFilm = "SELECT * FROM films WHERE film_id = ?";
        jdbcTemplate.update(findFilm, id);
        return getById(id);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        checkIfExist(filmId, userId);
        final String sqlQuery = "INSERT INTO likes (films_id, users_id) values (?,?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Лайк от пользователя " + userId + " поставлен фильму " + filmId);
        return getById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        checkIfExist(filmId, userId);
        final String sqlQuery = "DELETE FROM likes " +
                "WHERE films_id = ? AND users_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Лайк от пользователя " + userId + " удален у фильма " + filmId);
        return getById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration " +
                "FROM films " +
                "LEFT JOIN likes fl ON films.film_id = fl.films_id " +
                "group by films.film_id, fl.films_id IN ( " +
                "    SELECT film_id " +
                "    FROM likes " +
                ") " +
                "ORDER BY COUNT(fl.films_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("film_id");
        final String name = resultSet.getString("name");
        final String description = resultSet.getString("description");
        final LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        return new Film(id, name, description, releaseDate, duration, findMpa(id), findGenres(id));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("genre_id");
        final String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("mpa_id");
        final String name = rs.getString("name");
        return new Mpa(id, name);
    }

    private Mpa findMpa(int filmId) {
        final String mpaSqlQuery = "SELECT mpa_id, name " +
                "FROM mpa " +
                "LEFT JOIN films_mpa MF ON mpa.mpa_id = mf.rating_mpa_id " +
                "WHERE films_id = ?";
        return jdbcTemplate.queryForObject(mpaSqlQuery, this::makeMpa, filmId);
    }

    private List<Genre> findGenres(int filmId) {
        final String genresSqlQuery = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genre fg on genre.genre_id = fg.genres_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId);
    }

    private void checkIfExist(int filmId, int userId) {
        final String checkUserFilmQuery = "SELECT 1 where exists(select * FROM users WHERE user_id = ?) " +
                "and exists(select * FROM films WHERE film_id = ?)";
        SqlRowSet filmUserRows = jdbcTemplate.queryForRowSet(checkUserFilmQuery, userId, filmId);
        if (!filmUserRows.next()) {
            log.info("Фильм или пользователь не найден.", userId, filmId);
            throw new NotFoundException("Фильм или пользователь не найден");
        }
    }
}