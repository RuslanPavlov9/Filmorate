package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.dao.Film;
import ru.yandex.practicum.filmorate.repository.dao.Genre;
import ru.yandex.practicum.filmorate.repository.dao.Like;
import ru.yandex.practicum.filmorate.repository.dao.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    //private final FilmMapper filmMapper;
    private final GenreRepository genreRepository;

    @Transactional
    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        if (likeRepository.existsByFilmAndUser(film, user)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        Like like = new Like();
        like.setFilm(film);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        if (!likeRepository.existsByFilmAndUser(film, user)) {
            throw new NotFoundException("Лайк не найден");
        }

        likeRepository.deleteByFilmAndUser(film, user);
    }

    @Transactional(readOnly = true)
    public List<Film> getPopularFilms(int count) {
        return filmRepository.findPopularFilms(count);
    }

    @Transactional
    public Film addFilm(Film film) {
        validateFilm(film);
        return filmRepository.save(film);
    }

    @Transactional
    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!filmRepository.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        return filmRepository.save(film);
    }

    @Transactional(readOnly = true)
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Film getFilmById(int id) {
       filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmRepository.getOne(id);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        // Проверка MPA (если нужно)
        if (film.getMpa() == null || film.getMpa().getId() == 0) {
            throw new ValidationException("MPA рейтинг должен быть указан");
        }

        // Проверка жанров
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            List<Integer> existingIds = genreRepository.findAllById(genreIds)
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());

            // Находим ID жанров, которых нет в базе
            Set<Integer> missingIds = genreIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toSet());

            if (!missingIds.isEmpty()) {
                throw new ValidationException("Следующие ID жанров не существуют: " + missingIds);
            }
        }
    }

}