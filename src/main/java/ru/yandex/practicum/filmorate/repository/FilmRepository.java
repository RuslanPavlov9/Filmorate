package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.filmorate.repository.dao.Film;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Integer> {

    @Query("SELECT f FROM Film f LEFT JOIN f.likes l GROUP BY f ORDER BY COUNT(l) DESC")
    List<Film> findPopularFilms(@Param("count") int count);
}