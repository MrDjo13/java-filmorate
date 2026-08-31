package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Не указан ID фильма для обновления");
            throw new ValidationException("ID фильма должен быть указан");
        }
        Film oldFilm = getById(film.getId());
        validateReleaseDate(film);

        if (film.getLikes().isEmpty() && !oldFilm.getLikes().isEmpty()) {
            film.setLikes(oldFilm.getLikes());
        }

        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Пользователь ID {} поставил лайк фильму ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Пользователь ID {} убрал лайк с фильма ID {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH_DATE)) {
            log.warn("Валидация не пройдена: дата релиза {} раньше {}", film.getReleaseDate(), CINEMA_BIRTH_DATE);
            throw new ValidationException("Дата релиза не может быть раньше " + CINEMA_BIRTH_DATE);
        }
    }
}