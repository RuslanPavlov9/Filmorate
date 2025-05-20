package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String loadJson(String filename) throws IOException {
        return StreamUtils.copyToString(
                getClass().getResourceAsStream("/test-data/films/" + filename),
                StandardCharsets.UTF_8);
    }

    @Test
    void addFilm_ValidFilm_Returns200() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("valid-film.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valid Film"));
    }

    @Test
    void addFilm_EmptyName_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-name.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Название фильма не может быть пустым"));
    }

    @Test
    void addFilm_LongDescription_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("long-description.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Описание не может быть длиннее 200 символов"));
    }

    @Test
    void addFilm_MissingReleaseDate_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("missing-release-date.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Дата релиза обязательна"));
    }

    @Test
    void addFilm_EarlyReleaseDate_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("early-release-date.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void addFilm_NegativeDuration_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("negative-duration.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void addFilm_ZeroDuration_Returns400() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("zero-duration.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void updateFilm_NonExistingId_Returns400() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("non-existing-id.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Фильм с id 999 не найден"));
    }

    @Test
    void getAllFilms_Returns200() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}