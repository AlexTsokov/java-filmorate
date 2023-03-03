package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    public final Set<Integer> friends = new HashSet<>();
}
