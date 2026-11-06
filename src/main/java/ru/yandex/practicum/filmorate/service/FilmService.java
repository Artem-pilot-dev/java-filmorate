package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmResponseDto addFilm(FilmRequestDto dto) {
        Film film = FilmMapper.toFilm(dto);
        validateFilm(film);
        Film savedFilm = filmStorage.addFilm(film);
        return FilmMapper.toFilmResponseDto(savedFilm);
    }

    public FilmResponseDto updateFilm(FilmRequestDto dto) {
        Film film = FilmMapper.toFilm(dto);
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        return FilmMapper.toFilmResponseDto(updatedFilm);
    }

    private Film getStorageFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public FilmResponseDto getFilmById(Long filmId) {
        Film film = getStorageFilmById(filmId);
        return FilmMapper.toFilmResponseDto(film);
    }

    public List<FilmResponseDto> getAllFilms() {
        return filmStorage.getAllFilms().stream()
                .map(film -> FilmMapper.toFilmResponseDto(film))
                .toList();
    }

    public FilmResponseDto addLike(Long filmId, Long userId) {
        Film film = getStorageFilmById(filmId);
        userStorage.getUserById(userId);
        boolean added = film.getLikes().add(userId);
        if (added) {
            log.info("Пользователь с id = {} добавил лайк  фильму {}", userId, film.getName());
        } else {
            log.debug("Лайк от пользователя {} уже существует к фильму {}", userId, film.getName());
        }
        return FilmMapper.toFilmResponseDto(film);
    }

    public FilmResponseDto removeLike(Long filmId, Long userId) {
        Film film = getStorageFilmById(filmId);
        userStorage.getUserById(userId);
        boolean removed = film.getLikes().remove(userId);
        if (removed) {
            log.info("Лайк пользователя c id = {} к фильму {} был удален", userId, film.getName());
        } else {
            log.debug("Такого лайка от пользователя с id = {} к фильму {} не было", userId, film.getName());
        }
        return FilmMapper.toFilmResponseDto(film);
    }

    public List<FilmResponseDto> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть больше 0");
        }
        Comparator<Film> byLikes = Comparator.comparingInt(film -> film.getLikes().size());
        return filmStorage.getAllFilms().stream()
                .sorted(byLikes.reversed()).map(film -> FilmMapper.toFilmResponseDto(film)).limit(count).toList();

    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate()
                .isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма с id = {} раньше 28 декабря 1895 года", film.getId());
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }
}
