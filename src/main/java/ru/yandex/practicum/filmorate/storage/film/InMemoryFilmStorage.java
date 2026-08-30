package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        film.setId(++currentId);
        films.put(film.getId(), film);
        log.info("Фильм успешно создан с ID: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            log.warn("Фильм с ID {} не найден для обновления", film.getId());
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        validateReleaseDate(film);
        if (film.getLikes().isEmpty() && !oldFilm.getLikes().isEmpty()) {
            film.setLikes(oldFilm.getLikes());
        }
        films.put(film.getId(), film);
        log.info("Фильм с ID {} успешно обновлен", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
        log.info("Фильм с ID {} удален", id);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(Film.CINEMA_BIRTH_DATE)) {
            log.warn("Валидация не пройдена: дата релиза {} раньше {}", film.getReleaseDate(), Film.CINEMA_BIRTH_DATE);
            throw new ValidationException("Дата релиза не может быть раньше " + Film.CINEMA_BIRTH_DATE);
        }
    }
}