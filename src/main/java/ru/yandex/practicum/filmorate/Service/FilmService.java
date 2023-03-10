package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    public final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public ResponseEntity<Film> addLike(Integer userId, Integer filmId) throws NotFoundException {
        filmStorage.addLike(filmId, userId);
        log.info("Лайк добавлен");
        return new ResponseEntity<>(filmStorage.getById(filmId), HttpStatus.OK);
    }

    public ResponseEntity<Film> deleteLike(Integer filmId, Integer userId) throws NotFoundException {
        filmStorage.removeLike(filmId, userId);
        log.info("Лайк удален");
        return new ResponseEntity<>(filmStorage.getById(filmId), HttpStatus.OK);
    }

    public List<Film> getTopLikedFilmsList(Integer count) {
        return filmStorage.getBestFilms(count);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getById(id);
    }

    public Film deleteById(int id) {
        return filmStorage.deleteFilm(id);
    }
}
