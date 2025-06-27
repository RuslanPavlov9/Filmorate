package ru.yandex.practicum.filmorate.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.repository.dao.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MpaService.class)
class MpaServiceIntegrationTest {

    @Autowired
    private MpaService mpaService;

    @Test
    void getAllMpa_shouldReturnAllMpaOrderedById() {
        // Act
        List<Mpa> result = mpaService.getAllMpa();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Проверяем порядок по id
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getId() < result.get(i + 1).getId());
        }
    }

    @Test
    void getMpaById_shouldReturnMpa_whenExists() {
        // Arrange
        int existingId = 1;

        // Act
        Mpa result = mpaService.getMpaById(existingId);

        // Assert
        assertNotNull(result);
        assertEquals(existingId, result.getId());
    }

    @Test
    void getMpaById_shouldThrowNotFoundException_whenNotExists() {
        // Arrange
        int nonExistingId = 999;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> mpaService.getMpaById(nonExistingId));
    }

    @Test
    void getAllMpa_shouldReturnCorrectMpaData() {
        // Act
        List<Mpa> result = mpaService.getAllMpa();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 5); // Проверяем, что есть как минимум 5 рейтингов

        // Проверяем, что первый элемент - G
        assertEquals("G", result.get(0).getName());
        assertEquals(1, result.get(0).getId());

        // Проверяем порядок и основные рейтинги
        assertEquals("PG", result.get(1).getName());
        assertEquals("PG-13", result.get(2).getName());
        assertEquals("R", result.get(3).getName());
        assertEquals("NC-17", result.get(4).getName());
    }
}