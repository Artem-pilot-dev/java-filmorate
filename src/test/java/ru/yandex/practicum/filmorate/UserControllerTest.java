package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController controller;
    private User user;
    private Validator validator;

    @BeforeEach
    public void createUserControllerAndUser() {
        controller = new UserController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
        user.setEmail("Lion@gmail.com");
        user.setLogin("Lion");
        user.setName("Tom");
        user.setBirthday(LocalDate.of(1999, 12, 15));
    }

    @Test
    public void shouldRejectUserWithEmailIsNull() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailIsEmpty() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailIsBlank() {
        user.setEmail(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailWithoutAtSign() {
        user.setEmail("Liongmail.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsNull() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsEmpty() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsBlank() {
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginContainingSpace() {
        user.setLogin("Tom Sawyer");
        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void shouldRejectUserWithBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAcceptUserWithBirthdayNow() {
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void userWithoutNameReceivesLoginName() {
        user.setName(null);
        User newUser = controller.createUser(user);
        assertEquals("Lion", newUser.getName());
    }

    @Test
    public void userWithEmptyNameReceivesLoginName() {
        user.setName("");
        User newUser = controller.createUser(user);
        assertEquals("Lion", newUser.getName());
    }

    @Test
    public void userWithNameIsBlankReceivesLoginName() {
        user.setName(" ");
        User newUser = controller.createUser(user);
        assertEquals("Lion", newUser.getName());
    }

    @Test
    public void userWithNameAfterCreateHasTheSameName() {
        User newUser = controller.createUser(user);
        assertEquals("Tom", newUser.getName());
    }

    @Test
    public void checkFormatEmailOfUser() {
        user.setEmail("lion@");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

}
