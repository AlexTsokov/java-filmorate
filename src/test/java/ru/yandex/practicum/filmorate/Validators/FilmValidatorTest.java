package ru.yandex.practicum.filmorate.Validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    Film film;
    Mpa mpa = new Mpa(1, "16+");
    Genre genre = new Genre(1, "Ужасы");
    List<Genre> genreList = List.of(genre);

    void initFilm() {
        film = new Film(12,"Унесенные ветром", "Описание до 200 символов",
                LocalDate.of(1985, 12,22), 200, mpa, genreList);
    }

    @Test
    void filmNameCantBeEmpty() {
        initFilm();
        Film film2 = new Film(12,"", "Описание до 200 символов",
                LocalDate.of(1985, 12,22), 200, mpa, genreList);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
    }

    @Test
    void filmDescriptionCantBeNullOrMoreThan200Characters() {
        initFilm();
        Film film2 = new Film( 1,"Аватар", "Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов. " +
                "Описание больше 200 символов. Описание больше 200 символов. Описание больше 200 символов.",
                LocalDate.of(2009, 12,22), 200, mpa, genreList);
        Film film3 = new Film( 2,"Аватар", null,
                LocalDate.of(2009, 12,22), 200, mpa, genreList);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertFalse(FilmValidator.validate(film3));
    }

    @Test
    void filmReleaseDateShouldBeAfter18951228() {
        initFilm();
        Film film2 = new Film( 1,"Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12,27), 200, mpa, genreList);
        Film film3 = new Film( 2, "Аватар", "Описание до 200 символов",
                LocalDate.of(1895, 12,28), 200, mpa, genreList);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertTrue(FilmValidator.validate(film3));
    }

    @Test
    void filmDurationShouldBePositive() {
        initFilm();
        Film film2 = new Film(1, "Аватар", "Описание до 200 символов",
                LocalDate.of(1995, 12,27), -200, mpa, genreList);
        Film film3 = new Film(2, "Аватар2", "Описание до 200 символов",
                LocalDate.of(1995, 12,28), 0, mpa, genreList);
        assertTrue(FilmValidator.validate(film));
        assertFalse(FilmValidator.validate(film2));
        assertFalse(FilmValidator.validate(film3));
    }
}