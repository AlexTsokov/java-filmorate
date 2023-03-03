package ru.yandex.practicum.filmorate.Exception;

public class NotFoundException extends NullPointerException {
    public NotFoundException(String s) {
        super(s);
    }
}
