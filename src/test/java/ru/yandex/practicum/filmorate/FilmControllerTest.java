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

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());

        Film created = filmController.create(film);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("   ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(Film.MAX_DESCRIPTION_LENGTH + 1));
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenReleaseDateIsBeforeCinemaBirth() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldAllowReleaseDateOnCinemaBirth() {
        Film film = new Film();
        film.setName("First Film Ever");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Film created = filmController.create(film);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldFailValidationWhenDurationIsNegativeOrZero() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}