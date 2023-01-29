package ru.yandex.practicum.filmorate.Exception;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String s) {
        super(s);
    }
}
