package ru.yandex.practicum.filmorate.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> findAll() {
        final String findAll = "SELECT * FROM genre";
        return jdbcTemplate.query(findAll, this::genreMaker);
    }

    @Override
    public Genre getById(int id) {
        final String getGenre = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(getGenre, id);
        if (!sqlRowSet.next()) {
            log.info("Жанр не найден", id);
            throw new NotFoundException("Жанр не найден");
        }
        final String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::genreMaker, id);
    }

    private Genre genreMaker(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("genre_id");
        final String name = rs.getString("name");
        return new Genre(id, name);
    }
}
