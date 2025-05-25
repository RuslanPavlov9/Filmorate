package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int nextUserId = 1;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        log.info("Попытка создания пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        try {
            validateUser(user);
            user.setId(nextUserId++);
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.add(user);
            log.info("Пользователь создан: id={}, email={}", user.getId(), user.getEmail());
            return ResponseEntity.ok(user);
        } catch (ValidationException ex) {
            log.error("Ошибка при создании пользователя: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Ошибка при создании пользователя",
                    ex.getMessage(),
                    Instant.now().toString()
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        log.info("Попытка обновления пользователя: id={}", updatedUser.getId());
        try {
            validateUser(updatedUser);
            for (User user : users) {
                if (user.getId() == updatedUser.getId()) {
                    user.setEmail(updatedUser.getEmail());
                    user.setLogin(updatedUser.getLogin());
                    user.setName(updatedUser.getName() != null ? updatedUser.getName() : user.getLogin());
                    user.setBirthday(updatedUser.getBirthday());
                    log.info("Пользователь обновлён: id={}", user.getId());
                    return ResponseEntity.ok(user);
                }
            }
            String error = "Пользователь с id " + updatedUser.getId() + " не найден";
            log.warn(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            HttpStatus.NOT_FOUND.value(),
                            "Пользователь не найден",
                            error,
                            Instant.now().toString()
                    ));
        } catch (ValidationException ex) {
            log.error("Ошибка при обновлении пользователя: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Ошибка валидации",
                    ex.getMessage(),
                    Instant.now().toString()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("Запрос списка всех пользователей (найдено: {})", users.size());
        return ResponseEntity.ok(users);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}