package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

public class FilmMapper {
    private FilmMapper() {

    }

    public static Film toFilm(FilmRequestDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        return film;
    }

    public static FilmResponseDto toFilmResponseDto(Film film) {
        FilmResponseDto dto = new FilmResponseDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(new HashSet<>(film.getLikes()));
        return dto;
    }
}
