package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.dao.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, id);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDto>> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        List<FilmDto> films = filmService.getPopularFilms(count).stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<FilmDto> addFilm(@RequestBody FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        Film createdFilm = filmService.addFilm(film);
        FilmDto resultDto = filmMapper.toDto(createdFilm);
        log.info("Фильм добавлен: {}", resultDto);
        return ResponseEntity.ok(resultDto);
    }

    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@RequestBody FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        Film updatedFilm = filmService.updateFilm(film);
        FilmDto resultDto = filmMapper.toDto(updatedFilm);
        log.info("Фильм обновлён: {}", resultDto);
        return ResponseEntity.ok(resultDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable int id) {
        Film film = filmService.getFilmById(id);
        FilmDto resultDto = filmMapper.toDto(film);
        return ResponseEntity.ok(resultDto);
    }

    @GetMapping
    public ResponseEntity<List<FilmDto>> getAllFilms() {
        List<FilmDto> films = filmService.getAllFilms().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(films);
    }

}