package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<FilmResponseDto> addFilm(@Valid @RequestBody FilmRequestDto dto) {
        FilmResponseDto addedFilm = filmService.addFilm(dto);
        log.info("Добавлен фильм с id {}", addedFilm.getId());
        return ResponseEntity.ok(addedFilm);
    }

    @GetMapping
    public ResponseEntity<List<FilmResponseDto>> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmResponseDto> getFilmById(@PathVariable Long id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PutMapping
    public ResponseEntity<FilmResponseDto> updateFilm(@Valid @RequestBody FilmRequestDto dto) {
        FilmResponseDto updatedFilm = filmService.updateFilm(dto);
        log.info("Обновлен фильм с id {}", updatedFilm.getId());
        return ResponseEntity.ok(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmResponseDto> addLike(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(filmService.addLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmResponseDto> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(filmService.removeLike(id, userId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmResponseDto>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }
}
