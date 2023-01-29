package ru.yandex.practicum.filmorate.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.Validators.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<String, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> allFilms() {
        return films.values();
    }

    @PostMapping(value = "/film")
    public Film create(@RequestBody Film film) {
        if (!FilmValidator.validate(film))
            throw new ValidationException("Ошибка валидации");
        if (films.containsKey(film.getName())) {
            throw new FilmAlreadyExistException("Фильм " +
                    film.getName() + " уже добавлен.");
        } else films.put(film.getName(), film);
        log.info("Добавлен фильм " + film.getName());
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if (!FilmValidator.validate(film))
            throw new ValidationException("Ошибка валидации");
        else films.put(film.getName(), film);
        log.info("Обновлены данные фильма " + film.getName());
        return film;
    }
}
