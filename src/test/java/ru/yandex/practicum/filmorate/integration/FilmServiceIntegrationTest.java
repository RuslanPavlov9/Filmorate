package ru.yandex.practicum.filmorate.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.repository.dao.*;

import jakarta.persistence.EntityManager;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FilmService.class)
@ActiveProfiles("test")
class FilmServiceIntegrationTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private EntityManager entityManager;

    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM likes").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM film_genres").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM films").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();

        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setLogin("userlogin");
        testUser.setName("User Name");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        entityManager.persist(testUser);

        Mpa mpa = entityManager.find(Mpa.class, 3);

        Genre genre = entityManager.find(Genre.class, 1);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);
        testFilm.setGenres(Set.of(genre));
        entityManager.persist(testFilm);

        entityManager.flush();
    }

    @Test
    void addFilm_shouldSaveFilmWithValidData() {

        Mpa mpa = entityManager.find(Mpa.class, 2);

        Genre genre = entityManager.find(Genre.class, 2);

        Film newFilm = new Film();
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(120);
        newFilm.setMpa(mpa);
        newFilm.setGenres(Set.of(genre));

        Film savedFilm = filmService.addFilm(newFilm);

        assertNotNull(savedFilm.getId());
        assertEquals("New Film", savedFilm.getName());
        assertEquals(2, savedFilm.getMpa().getId());
        assertEquals(1, savedFilm.getGenres().size());
        assertEquals("Драма", savedFilm.getGenres().iterator().next().getName());
    }

    @Test
    void validateFilm_shouldCheckMpaExistence() {
        Film filmWithInvalidMpa = new Film();
        filmWithInvalidMpa.setName("Film with invalid MPA");
        filmWithInvalidMpa.setMpa(new Mpa(999, "Invalid"));
        filmWithInvalidMpa.setDuration(120);
        filmWithInvalidMpa.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithInvalidMpa.setGenres(Set.of(entityManager.find(Genre.class, 1)));

        assertThrows(NotFoundException.class, () -> filmService.addFilm(filmWithInvalidMpa));
    }

    @Test
    void validateFilm_shouldCheckGenresExistence() {
        Film filmWithInvalidGenre = new Film();
        filmWithInvalidGenre.setName("Film with invalid genre");
        filmWithInvalidGenre.setMpa(entityManager.find(Mpa.class, 1));
        filmWithInvalidGenre.setGenres(Set.of(new Genre(999, "Invalid")));

        assertThrows(ValidationException.class, () -> filmService.addFilm(filmWithInvalidGenre));
    }

    @Test
    void getPopularFilms_shouldReturnFilmsOrderedByLikes() {
        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setMpa(entityManager.find(Mpa.class, 3));
        secondFilm.setGenres(Set.of(entityManager.find(Genre.class, 2)));
        entityManager.persist(secondFilm);

        User secondUser = new User();
        secondUser.setEmail("user2@example.com");
        secondUser.setLogin("userlogin2");
        secondUser.setName("userlogin2");
        entityManager.persist(secondUser);

        entityManager.flush();

        filmService.addLike(testFilm.getId(), testUser.getId());
        filmService.addLike(secondFilm.getId(), testUser.getId());
        filmService.addLike(secondFilm.getId(), secondUser.getId());

        List<Film> popularFilms = filmService.getPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertEquals(secondFilm.getId(), popularFilms.get(0).getId());
        assertEquals(testFilm.getId(), popularFilms.get(1).getId());
    }

    @Test
    void getAllFilms_shouldReturnAllFilms() {
        List<Film> films = filmService.getAllFilms();

        assertFalse(films.isEmpty());
        assertEquals(testFilm.getName(), films.get(0).getName());
    }
}