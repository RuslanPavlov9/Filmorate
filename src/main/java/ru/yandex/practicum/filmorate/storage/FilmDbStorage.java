//package ru.yandex.practicum.filmorate.storage;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.repository.dao.Film;
//import ru.yandex.practicum.filmorate.repository.FilmRepository;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class FilmDbStorage implements FilmStorage {
//    private final FilmRepository filmRepository;
//
//    @Override
//    public Film addFilm(Film film) {
//        return filmRepository.save(film);
//    }
//
//    @Override
//    public Film updateFilm(Film film) {
//        if (!filmRepository.existsById(film.getId())) {
//            throw new NotFoundException("Фильм не найден");
//        }
//        return filmRepository.save(film);
//    }
//
//    @Override
//    public List<Film> getAllFilms() {
//        return filmRepository.findAll();
//    }
//
//    @Override
//    public Film getFilmById(int id) {
//        return filmRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
//    }
//}