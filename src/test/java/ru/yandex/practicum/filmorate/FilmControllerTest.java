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
        filmController = new FilmController();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        film = new Film();
        film.setName("Inception");
        film.setDescription("Mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
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
        film.setReleaseDate(FilmController.CINEMA_BIRTH_DATE);

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
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("releaseDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAllowDescriptionToBeNull() {
        film.setDescription(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowDurationToBeNull() {
        film.setDuration(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }
}