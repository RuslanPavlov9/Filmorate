package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.dao.Film;
import ru.yandex.practicum.filmorate.repository.dao.Like;
import ru.yandex.practicum.filmorate.repository.dao.User;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional
    public void addLike(Film film, User user) {
        Like like = new Like();
        like.setFilm(film);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(Film film, User user) {
        likeRepository.deleteByFilmAndUser(film, user);
    }
}