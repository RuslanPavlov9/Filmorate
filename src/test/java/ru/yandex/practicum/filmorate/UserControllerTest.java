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
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    void createUser_EmptyEmail_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-email.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email не может быть пустым"));
    }

    @Test
    void createUser_InvalidEmailFormat_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("invalid-email.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email должен содержать символ @"));
    }

    @Test
    void createUser_EmptyLogin_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-login.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Логин не может быть пустым"));
    }

    @Test
    void createUser_LoginWithSpaces_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("login-with-spaces.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Логин не может содержать пробелы"));
    }

    @Test
    void createUser_FutureBirthday_Returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("future-birthday.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Дата рождения не может быть в будущем"));
    }

    @Test
    void createUser_EmptyName_UsesLoginAsName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("empty-name.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void updateUser_NonExistingId_Returns400() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loadJson("non-existing-id.json")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь с id 999 не найден"));
    }
}