package ru.yandex.practicum.filmorate.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Integer, Film> films = new HashMap<>();
    Integer filmId = 0;

    @Override
    public List<Film> getAllFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        if (!FilmValidator.validate(film))
            throw new ValidationException("Ошибка валидации");
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Фильм " +
                    film.getName() + " уже добавлен.");
        } else {
            Integer id = generateFilmId();
            film.setId(id);
            films.put(film.getId(), film);
            log.info("Добавлен фильм " + film.getName());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!FilmValidator.validate(film))
            throw new ValidationException("Ошибка валидации");
        else {
            FilmValidator.checkIfFilmExists(film, films);
            films.put(film.getId(), film);
            log.info("Обновлены данные фильма " + film.getName());
        }
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    public Integer generateFilmId() {
        filmId++;
        return filmId;
    }
}
