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
        List<Mpa> result = mpaService.getAllMpa();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getId() < result.get(i + 1).getId());
        }
    }

    @Test
    void getMpaById_shouldReturnMpa_whenExists() {

        int existingId = 1;

        Mpa result = mpaService.getMpaById(existingId);

        assertNotNull(result);
        assertEquals(existingId, result.getId());
    }

    @Test
    void getMpaById_shouldThrowNotFoundException_whenNotExists() {

        int nonExistingId = 999;

        assertThrows(NotFoundException.class, () -> mpaService.getMpaById(nonExistingId));
    }

    @Test
    void getAllMpa_shouldReturnCorrectMpaData() {

        List<Mpa> result = mpaService.getAllMpa();

        assertNotNull(result);
        assertTrue(result.size() >= 5);

        assertEquals("G", result.get(0).getName());
        assertEquals(1, result.get(0).getId());

        assertEquals("PG", result.get(1).getName());
        assertEquals("PG-13", result.get(2).getName());
        assertEquals("R", result.get(3).getName());
        assertEquals("NC-17", result.get(4).getName());
    }
}