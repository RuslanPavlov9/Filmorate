package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextFilmId = 1;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public ResponseEntity<?> addFilm(@RequestBody Film film) {
        log.info("Попытка добавления фильма: name={}", film.getName());
        try {
            validateFilm(film);
            film.setId(nextFilmId++);
            films.add(film);
            log.info("Фильм добавлен: id={}, name={}", film.getId(), film.getName());
            return ResponseEntity.ok(film);
        } catch (ValidationException ex) {
            log.error("Ошибка при добавлении фильма: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Ошибка при добавлении фильма",
                    ex.getMessage(),
                    Instant.now().toString()
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film updatedFilm) {
        log.info("Попытка обновления фильма: id={}", updatedFilm.getId());
        try {
            validateFilm(updatedFilm);
            for (Film film : films) {
                if (film.getId() == updatedFilm.getId()) {
                    film.setName(updatedFilm.getName());
                    film.setDescription(updatedFilm.getDescription());
                    film.setReleaseDate(updatedFilm.getReleaseDate());
                    film.setDuration(updatedFilm.getDuration());
                    log.info("Фильм обновлён: id={}", film.getId());
                    return ResponseEntity.ok(film);
                }
            }
            String error = "Фильм с id " + updatedFilm.getId() + " не найден";
            log.warn(error);
            return ResponseEntity.status(404).body(new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Ошибка при поиске фильма",
                    error,
                    Instant.now().toString()
            ));
        } catch (ValidationException ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Ошибка при обновлении фильма",
                    ex.getMessage(),
                    Instant.now().toString()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Запрос списка всех фильмов (найдено: {})", films.size());
        return ResponseEntity.ok(films);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String error = "Название фильма не может быть пустым";
            log.warn("Валидация фильма не пройдена: {}", error);
            throw new ValidationException(error);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String error = "Описание не может быть длиннее 200 символов";
            log.warn("Валидация фильма не пройдена: {}", error);
            throw new ValidationException(error);
        }
        if (film.getReleaseDate() == null) {
            String error = "Дата релиза обязательна";
            log.warn("Валидация фильма не пройдена: {}", error);
            throw new ValidationException(error);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String error = "Дата релиза не может быть раньше 28 декабря 1895 года";
            log.warn("Валидация фильма не пройдена: {}", error);
            throw new ValidationException(error);
        }
        if (film.getDuration() <= 0) {
            String error = "Продолжительность фильма должна быть положительной";
            log.warn("Валидация фильма не пройдена: {}", error);
            throw new ValidationException(error);
        }
    }

}