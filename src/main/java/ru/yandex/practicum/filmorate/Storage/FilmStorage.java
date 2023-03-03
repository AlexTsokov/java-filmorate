package ru.yandex.practicum.filmorate.Storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer id);

    List<Film> getAllFilmsList();

    Map<Integer, Film> getFilms();

    Film getById(Integer id);
}
