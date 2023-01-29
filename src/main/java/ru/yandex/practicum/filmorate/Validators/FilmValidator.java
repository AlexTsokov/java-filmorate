package ru.yandex.practicum.filmorate.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    private static final Logger log = LoggerFactory.getLogger(FilmValidator.class);

    public static boolean validate(Film film) {
        if (film.getName().isBlank()) {
            log.error("Имя фильма не заполнено");
            return false;
        } else if (film.getDescription().length() > 200) {
            log.error("Длина описания больше 200 символов");
            return false;
        } else if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Дата релиза раньше 1895-12-28");
            return false;
        } else if (film.getDuration() <= 0) {
            log.error("Длительность меньше 0");
            return false;
        } else return true;
    }
}
