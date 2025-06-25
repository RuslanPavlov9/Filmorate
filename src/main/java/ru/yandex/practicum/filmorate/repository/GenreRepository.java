package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.repository.dao.Genre;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}