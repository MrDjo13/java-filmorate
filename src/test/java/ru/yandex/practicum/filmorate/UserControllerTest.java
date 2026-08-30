package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;
    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        validUser = User.builder()
                .email("test@example.com")
                .login("usrname")
                .name("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
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
        assertThrows(NullPointerException.class, () -> validUser.setEmail(null));
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
        assertThrows(NullPointerException.class, () -> validUser.setLogin(null));
    }

    @Test
    void shouldFailValidationWhenBirthdayIsInFuture() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertEquals("birthday", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenBirthdayIsNull() {
        assertThrows(NullPointerException.class, () -> validUser.setBirthday(null));
    }
}