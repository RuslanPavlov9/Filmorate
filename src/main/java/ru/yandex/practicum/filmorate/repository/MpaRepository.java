package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.repository.dao.Mpa;

public interface MpaRepository extends JpaRepository<Mpa, Integer> {
}