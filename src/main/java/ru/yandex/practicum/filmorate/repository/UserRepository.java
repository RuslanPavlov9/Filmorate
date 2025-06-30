package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.repository.dao.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}