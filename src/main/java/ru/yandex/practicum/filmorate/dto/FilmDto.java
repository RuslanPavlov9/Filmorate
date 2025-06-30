package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.repository.dao.Genre;
import ru.yandex.practicum.filmorate.repository.dao.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmDto {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private Set<Genre> genres;
    private int likesCount;
}