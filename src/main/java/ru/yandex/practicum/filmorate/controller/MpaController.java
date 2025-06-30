package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.repository.dao.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;
    private final MpaMapper mpaMapper;

    @GetMapping
    public ResponseEntity<List<MpaDto>> getAllMpa() {
        List<MpaDto> mpaList = mpaService.getAllMpa().stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mpaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaDto> getMpaById(@PathVariable int id) {
        Mpa mpa = mpaService.getMpaById(id);
        return ResponseEntity.ok(mpaMapper.toDto(mpa));
    }
}