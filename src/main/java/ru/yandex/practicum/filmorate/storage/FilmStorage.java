package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film getFilmById(Long filmId);

    List<Film> getAllFilms();

    Film updateFilm(Film film);

    void deleteFilm(Long filmId);

}
