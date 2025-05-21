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
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String loadJson(String filename) throws IOException {
        return StreamUtils.copyToString(
                getClass().getResourceAsStream("/test-data/users/" + filename),
                StandardCharsets.UTF_8);
    }

    @Test
    void createUser_ValidUser_Returns200() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("valid-user.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("valid@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createUser_EmptyEmail_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-email.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Ошибка при создании пользователя"))
                .andExpect(jsonPath("$.message").value("Email не может быть пустым"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createUser_InvalidEmailFormat_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("invalid-email.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Ошибка при создании пользователя"))
                .andExpect(jsonPath("$.message").value("Email должен содержать символ @"));
    }

    @Test
    void createUser_EmptyLogin_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-login.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Ошибка при создании пользователя"))
                .andExpect(jsonPath("$.message").value("Логин не может быть пустым"));
    }

    @Test
    void createUser_LoginWithSpaces_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("login-with-spaces.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Ошибка при создании пользователя"))
                .andExpect(jsonPath("$.message").value("Логин не может содержать пробелы"));
    }

    @Test
    void createUser_FutureBirthday_Returns400() throws Exception {
        String json = loadJson("future-birthday.json")
                .replace("${futureDate}", LocalDate.now().plusDays(1).toString());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Ошибка при создании пользователя"))
                .andExpect(jsonPath("$.message").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void createUser_EmptyName_UsesLoginAsName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-name.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.login").value("login"));
    }

    @Test
    void updateUser_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("non-existing-id.json")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Пользователь не найден"))
                .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден"));
    }

}