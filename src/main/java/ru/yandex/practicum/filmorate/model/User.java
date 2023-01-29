package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    @NonNull
    private int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;

    // Без данного инициированного конструктора почему-то не создать объект в тестах, не принимаеи имя юзера
    public User(@NonNull int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
