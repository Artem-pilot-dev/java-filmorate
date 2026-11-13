package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;
    private Validator validator;
    private FilmRequestDto dto;

    @BeforeEach
    public void createControllerAndFilm() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        dto = new FilmRequestDto();
        dto.setName("Tiger");
        dto.setDescription("Like tiger lived in garden");
        dto.setReleaseDate(LocalDate.of(1895, 12, 28));
        dto.setDuration(90);
    }

    @Test
    public void shouldRejectFilmWithReleaseDateBeforeMinimum() {
        dto.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.addFilm(dto));
    }

    @Test
    public void shouldAcceptFilmWithReleaseDateAtMinimum() {
        dto.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.addFilm(dto));
    }

    @Test
    public void shouldRejectFilmWithLengthOfDescriptionOver200Symbols() {
        dto.setDescription("a".repeat(201));
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAcceptFilmWithDescriptionLength200() {
        dto.setDescription("L".repeat(200));
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithDurationZero() {
        dto.setDuration(0);
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAcceptFilmWithDurationOne() {
        dto.setDuration(1);
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithDurationNull() {
        dto.setDuration(null);
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithDurationMinusOne() {
        dto.setDuration(-1);
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithNameNull() {
        dto.setName(null);
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithEmptyName() {
        dto.setName("");
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectFilmWithBlankName() {
        dto.setName(" ");
        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

}
