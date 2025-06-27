package ru.yandex.practicum.filmorate.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.repository.dao.User;

import jakarta.persistence.EntityManager;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserService.class)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Очищаем данные перед каждым тестом
        entityManager.createNativeQuery("DELETE FROM friends").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM likes").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();

        // Создаем тестовых пользователей
        testUser1 = createTestUser("user1@example.com", "user1", "User One");
        testUser2 = createTestUser("user2@example.com", "user2", "User Two");
        testUser3 = createTestUser("user3@example.com", "user3", "User Three");
    }

    private User createTestUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @Test
    void addUser_shouldSaveUserWithValidData() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("newlogin");
        newUser.setName("New User");

        User savedUser = userService.addUser(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("newlogin", savedUser.getLogin());
    }

    @Test
    void addUser_shouldSetLoginAsNameWhenNameIsEmpty() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("newlogin");
        newUser.setName(" ");

        User savedUser = userService.addUser(newUser);

        assertEquals("newlogin", savedUser.getName());
    }

    @Test
    void addUser_shouldThrowValidationExceptionWhenInvalidData() {
        User invalidUser = new User();
        invalidUser.setEmail("invalid-email");
        invalidUser.setLogin(" ");

        assertThrows(ValidationException.class, () -> userService.addUser(invalidUser));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        testUser1.setName("Updated Name");
        User updatedUser = userService.updateUser(testUser1);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(testUser1.getId(), updatedUser.getId());
    }

    @Test
    void updateUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        User nonExistingUser = new User();
        nonExistingUser.setId(999);
        nonExistingUser.setEmail("nonexisting@example.com");
        nonExistingUser.setLogin("nonexisting");

        assertThrows(NotFoundException.class, () -> userService.updateUser(nonExistingUser));
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        User foundUser = userService.getUserById(testUser1.getId());

        assertEquals(testUser1.getId(), foundUser.getId());
        assertEquals(testUser1.getEmail(), foundUser.getEmail());
    }

    @Test
    void getUserById_shouldThrowNotFoundExceptionWhenNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> users = userService.getAllUsers();

        assertEquals(3, users.size());
    }

    @Test
    void addFriend_shouldAddFriendWhenValid() {
        userService.addFriend(testUser1.getId(), testUser2.getId());

        List<User> friends = userService.getFriends(testUser1.getId());
        assertEquals(1, friends.size());
        assertEquals(testUser2.getId(), friends.get(0).getId());
    }

    @Test
    void addFriend_shouldThrowValidationExceptionWhenAddingSelf() {
        assertThrows(ValidationException.class,
                () -> userService.addFriend(testUser1.getId(), testUser1.getId()));
    }

    @Test
    void addFriend_shouldThrowValidationExceptionWhenDuplicateFriendship() {
        userService.addFriend(testUser1.getId(), testUser2.getId());

        assertThrows(ValidationException.class,
                () -> userService.addFriend(testUser1.getId(), testUser2.getId()));
    }

    @Test
    void removeFriend_shouldRemoveFriendWhenExists() {
        userService.addFriend(testUser1.getId(), testUser2.getId());
        userService.removeFriend(testUser1.getId(), testUser2.getId());

        List<User> friends = userService.getFriends(testUser1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {
        // user1 дружит с user2 и user3
        userService.addFriend(testUser1.getId(), testUser2.getId());
        userService.addFriend(testUser1.getId(), testUser3.getId());

        // user2 дружит с user3
        userService.addFriend(testUser2.getId(), testUser3.getId());

        List<User> commonFriends = userService.getCommonFriends(testUser1.getId(), testUser2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(testUser3.getId(), commonFriends.get(0).getId());
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListWhenNoCommonFriends() {
        // user1 дружит с user2
        userService.addFriend(testUser1.getId(), testUser2.getId());

        // user3 не имеет общих друзей с user1
        List<User> commonFriends = userService.getCommonFriends(testUser1.getId(), testUser3.getId());

        assertTrue(commonFriends.isEmpty());
    }
}