package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
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

    private String validFilmJson;
    private String emptyNameJson;
    private String longDescriptionJson;
    private String missingReleaseDateJson;
    private String earlyReleaseDateJson;
    private String negativeDurationJson;
    private String zeroDurationJson;
    private String nonExistingIdJson;

    @BeforeEach
    void setUp() throws IOException {
        validFilmJson = loadJson("valid-film.json");
        emptyNameJson = loadJson("empty-name.json");
        longDescriptionJson = loadJson("long-description.json");
        missingReleaseDateJson = loadJson("missing-release-date.json");
        earlyReleaseDateJson = loadJson("early-release-date.json");
        negativeDurationJson = loadJson("negative-duration.json");
        zeroDurationJson = loadJson("zero-duration.json");
        nonExistingIdJson = loadJson("non-existing-id.json");
    }

    private String loadJson(String filename) throws IOException {
        return StreamUtils.copyToString(
                getClass().getResourceAsStream("/test-data/films/" + filename),
                StandardCharsets.UTF_8);
    }

    @Test
    void addFilm_ShouldReturnOkAndFilmWithId_WhenFilmIsValid() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validFilmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Valid Film"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.releaseDate").exists())
                .andExpect(jsonPath("$.duration").exists());
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyNameJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Название фильма не может быть пустым"));
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenDescriptionIsTooLong() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(longDescriptionJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Описание не может быть длиннее 200 символов"));
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenReleaseDateIsMissing() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingReleaseDateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenReleaseDateIsTooEarly() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(earlyReleaseDateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenDurationIsNegative() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(negativeDurationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void addFilm_ShouldReturnBadRequest_WhenDurationIsZero() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(zeroDurationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Продолжительность фильма должна быть положительной"));
    }

    @Test
    void updateFilm_ShouldReturnNotFound_WhenFilmDoesNotExist() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistingIdJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Не найдено"))
                .andExpect(jsonPath("$.message").value("Не найден фильм для обновления"));
    }

    @Test
    void getAllFilms_ShouldReturnOkAndEmptyList_WhenNoFilmsExist() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

}