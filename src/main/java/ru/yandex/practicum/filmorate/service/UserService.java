package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundForDeleteException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.repository.dao.Friendship;
import ru.yandex.practicum.filmorate.repository.dao.User;
import ru.yandex.practicum.filmorate.repository.FriendshipRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.equals(friend)) {
            throw new ValidationException("Пользователь не может добавить в друзья самого себя");
        }

        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new ValidationException(String.format("Пользователь %s уже имеет в друзьях %s",
                    user.getName(),
                    friend.getName()));
        }

        Friendship friendship = new Friendship(user, friend);

        friendshipRepository.save(friendship);
    }

    @Transactional
    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new NotFoundForDeleteException(String.format(
                        "Пользователь %s не имеет в друзьях %s",
                        user.getName(),
                        friend.getName()
                )));

        friendshipRepository.delete(friendship);
    }

    @Transactional(readOnly = true)
    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        return friendshipRepository.findByUser(user).stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        Set<Integer> userFriends = getFriends(user.getId()).stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return getFriends(otherUser.getId()).stream()
                .filter(friend -> userFriends.contains(friend.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public User addUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        validateUser(user);
        if (!userRepository.existsById(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
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