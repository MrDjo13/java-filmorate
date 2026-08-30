package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;
    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("usrname");
        validUser.setName("John Doe");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty());

        User created = userController.create(validUser);

        assertEquals(1, created.getId());
    }

    @Test
    void shouldSetLoginAsNameIfNameIsEmpty() {
        validUser.setName("");

        User created = userController.create(validUser);

        assertEquals("usrname", created.getName());
    }

    @Test
    void shouldSetLoginAsNameIfNameIsNull() {
        validUser.setName(null);

        User created = userController.create(validUser);

        assertEquals("usrname", created.getName());
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        validUser.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenEmailIsNull() {
        validUser.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenLoginContainsSpaces() {
        validUser.setLogin("usr name");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertEquals("login", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenLoginIsNull() {
        validUser.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertEquals("login", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenBirthdayIsInFuture() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertEquals("birthday", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAllowNullBirthday() {
        validUser.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty());
    }
}