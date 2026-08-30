package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {

    private FilmController filmController;
    private Validator validator;
    private Film film;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        film = Film.builder()
                .name("Inception")
                .description("Mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
    }

    @Test
    void shouldCreateValidFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        Film created = filmController.create(film);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsTooLong() {
        film.setDescription("A".repeat(Film.MAX_DESCRIPTION_LENGTH + 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenReleaseDateIsBeforeCinemaBirth() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
    }

    @Test
    void shouldAllowReleaseDateOnCinemaBirth() {
        film.setReleaseDate(Film.CINEMA_BIRTH_DATE);

        Film created = filmController.create(film);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldFailValidationWhenDurationIsNegative() {
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("duration", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenReleaseDateIsNull() {
        assertThrows(NullPointerException.class, () -> film.setReleaseDate(null));
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        assertThrows(NullPointerException.class, () -> film.setName(null));
    }

    @Test
    void shouldFailValidationWhenDurationIsNull() {
        assertThrows(NullPointerException.class, () -> film.setDuration(null));
    }
}