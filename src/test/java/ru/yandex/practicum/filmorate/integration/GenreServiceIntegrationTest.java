package ru.yandex.practicum.filmorate.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.repository.dao.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(GenreService.class)
@ActiveProfiles("test")
class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void getAllGenres_shouldReturnGenresOrderedById() {

        List<Genre> result = genreService.getAllGenres();

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        assertEquals(3, result.get(2).getId());
    }

    @Test
    void getGenreById_shouldReturnGenre_whenExists() {
        Genre expected = new Genre(1, "Комедия");

        Genre result = genreService.getGenreById(1);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
    }

    @Test
    void getGenreById_shouldThrowNotFoundException_whenNotExists() {
        int nonExistingId = 999;

        assertThrows(NotFoundException.class, () -> genreService.getGenreById(nonExistingId));
    }

}