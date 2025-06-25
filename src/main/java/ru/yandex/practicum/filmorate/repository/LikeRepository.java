package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.filmorate.repository.dao.Film;
import ru.yandex.practicum.filmorate.repository.dao.Like;
import ru.yandex.practicum.filmorate.repository.dao.User;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    @Modifying
    @Query("DELETE FROM Like f WHERE f.film = :film AND f.user = :user")
    void deleteByFilmAndUser(@Param("film") Film film, @Param("user") User user);

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.film = :film AND l.user = :user")
    boolean existsByFilmAndUser(@Param("film") Film film, @Param("user") User user);

}