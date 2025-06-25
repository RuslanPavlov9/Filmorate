package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.repository.dao.Friendship;
import ru.yandex.practicum.filmorate.repository.dao.User;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    boolean existsByUserAndFriend(User user, User friend);

    List<Friendship> findByUser(User user);

    void deleteByUserAndFriend(User user, User friend);

    Optional<Friendship> findByUserAndFriend(User user, User friend);

}