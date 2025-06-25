package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.repository.dao.Mpa;

@Component
public class MpaMapper {
    public MpaDto toDto(Mpa mpa) {
        return new MpaDto(mpa.getId(), mpa.getName());
    }

    public Mpa toEntity(MpaDto mpaDto) {
        return new Mpa(mpaDto.getId(), mpaDto.getName());
    }
}