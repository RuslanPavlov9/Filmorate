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
        // Arrange
//        Genre genre1 = new Genre(1, "Комедия");
//        Genre genre2 = new Genre(2, "Драма");
//        Genre genre3 = new Genre(3, "Мультфильм");
//
//        entityManager.persist(genre2);
//        entityManager.persist(genre3);
//        entityManager.persist(genre1);
//        entityManager.flush();

        // Act
        List<Genre> result = genreService.getAllGenres();

        // Assert
        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        assertEquals(3, result.get(2).getId());
    }

    @Test
    void getGenreById_shouldReturnGenre_whenExists() {
        // Arrange
        Genre expected = new Genre(1, "Комедия");
//        entityManager.persist(expected);
//        entityManager.flush();

        // Act
        Genre result = genreService.getGenreById(1);

        // Assert
        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
    }

    @Test
    void getGenreById_shouldThrowNotFoundException_whenNotExists() {
        // Arrange
        int nonExistingId = 999;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> genreService.getGenreById(nonExistingId));
    }

}