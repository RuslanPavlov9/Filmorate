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
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String validUserJson;
    private String emptyEmailJson;
    private String invalidEmailJson;
    private String emptyLoginJson;
    private String loginWithSpacesJson;
    private String futureBirthdayJson;
    private String emptyNameJson;
    private String nonExistingIdJson;

    @BeforeEach
    void setUp() throws IOException {
        validUserJson = loadJson("valid-user.json");
        emptyEmailJson = loadJson("empty-email.json");
        invalidEmailJson = loadJson("invalid-email.json");
        emptyLoginJson = loadJson("empty-login.json");
        loginWithSpacesJson = loadJson("login-with-spaces.json");
        futureBirthdayJson = loadJson("future-birthday.json")
                .replace("${futureDate}", LocalDate.now().plusDays(1).toString());
        emptyNameJson = loadJson("empty-name.json");
        nonExistingIdJson = loadJson("non-existing-id.json");
    }

    private String loadJson(String filename) throws IOException {
        return StreamUtils.copyToString(
                getClass().getResourceAsStream("/test-data/users/" + filename),
                StandardCharsets.UTF_8);
    }

    @Test
    void createUser_ShouldReturnOkAndUserWithId_WhenUserIsValid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("valid@example.com"))
                .andExpect(jsonPath("$.login").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.birthday").exists());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyEmailJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Email не может быть пустым"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Email должен содержать символ @"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenLoginIsEmpty() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyLoginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Логин не может быть пустым"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenLoginContainsSpaces() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginWithSpacesJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Логин не может содержать пробелы"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenBirthdayIsInFuture() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(futureBirthdayJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void createUser_ShouldUseLoginAsName_WhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyNameJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.login").value("login"));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistingIdJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Не найдено"))
                .andExpect(jsonPath("$.message").value("Пользователь не найден"));
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Не найдено"));
    }
}