package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.dto.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController controller;
    private UserRequestDto dtoUser;
    private Validator validator;

    @BeforeEach
    public void createUserControllerAndUser() {

        controller = new UserController(new UserService(new InMemoryUserStorage()));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        dtoUser = new UserRequestDto();
        dtoUser.setEmail("Lion@gmail.com");
        dtoUser.setLogin("Lion");
        dtoUser.setName("Tom");
        dtoUser.setBirthday(LocalDate.of(1999, 12, 15));
    }

    @Test
    public void shouldRejectUserWithEmailIsNull() {
        dtoUser.setEmail(null);
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailIsEmpty() {
        dtoUser.setEmail("");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailIsBlank() {
        dtoUser.setEmail(" ");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithEmailWithoutAtSign() {
        dtoUser.setEmail("Liongmail.com");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsNull() {
        dtoUser.setLogin(null);
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsEmpty() {
        dtoUser.setLogin("");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginIsBlank() {
        dtoUser.setLogin(" ");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldRejectUserWithLoginContainingSpace() {
        dtoUser.setLogin("Tom Sawyer");
        assertThrows(ValidationException.class, () -> controller.createUser(dtoUser));
    }

    @Test
    public void shouldRejectUserWithBirthdayInFuture() {
        dtoUser.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAcceptUserWithBirthdayNow() {
        dtoUser.setBirthday(LocalDate.now());
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void userWithoutNameReceivesLoginName() {
        dtoUser.setName(null);
        ResponseEntity<UserResponseDto> newUser = controller.createUser(dtoUser);
        UserResponseDto body = newUser.getBody();
        assertNotNull(body);
        assertEquals("Lion", body.getName());
    }

    @Test
    public void userWithEmptyNameReceivesLoginName() {
        dtoUser.setName("");
        ResponseEntity<UserResponseDto> newUser = controller.createUser(dtoUser);
        UserResponseDto body = newUser.getBody();
        assertNotNull(body);
        assertEquals("Lion", body.getName());
    }

    @Test
    public void userWithNameIsBlankReceivesLoginName() {
        dtoUser.setName(" ");
        ResponseEntity<UserResponseDto> newUser = controller.createUser(dtoUser);
        UserResponseDto body = newUser.getBody();
        assertNotNull(body);
        assertEquals("Lion", body.getName());
    }

    @Test
    public void userWithNameAfterCreateHasTheSameName() {
        ResponseEntity<UserResponseDto> newUser = controller.createUser(dtoUser);
        UserResponseDto body = newUser.getBody();
        assertNotNull(body);
        assertEquals("Tom", body.getName());
    }

    @Test
    public void checkFormatEmailOfUser() {
        dtoUser.setEmail("lion@");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dtoUser);
        assertFalse(violations.isEmpty());
    }

}
