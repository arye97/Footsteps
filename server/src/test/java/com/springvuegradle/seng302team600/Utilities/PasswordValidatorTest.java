package com.springvuegradle.seng302team600.Utilities;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

    @Test
    public void validPasswordRegisteringTest() {
        String password = "password0";
        assertTrue(PasswordValidator.validate(password));
    }

    @Test
    public void validPasswordEditingTest() {
        String oldP = "password0";
        String newP = "password1";
        String repeat = "password1";
        assertTrue(PasswordValidator.validateEdit(oldP, newP, repeat));
    }

    //********************
    /**
     * The next two tests cover `PasswordValidator.validateRegEx` via `PasswordValidator.validate`.
     * So password rules from the regular expression
     * don't need tested for `PasswordValidator.validateEdit`, as it's already tested.
     */
    @Test
    public void invalidPasswordNoDigitRegisteringTest() {
        String password = "wrongPassword"; // Correct length but does not contain a digit
        assertThrows(ResponseStatusException.class, () -> PasswordValidator.validate(password));
    }

    @Test
    public void invalidPasswordLengthRegisteringTest() {
        String password = "wrong0"; // Contains a digit but less than 8 characters
        assertThrows(ResponseStatusException.class, () -> PasswordValidator.validate(password));
    }
    //********************

    @Test
    public void invalidPasswordOldMatchesNewTest() {
        String oldP = "password1";
        String newP = "password1";
        String repeat = "password1";
        assertThrows(ResponseStatusException.class, () -> PasswordValidator.validateEdit(oldP, newP, repeat));
    }

    @Test
    public void invalidPasswordNewDoesNotMatchRepeatTest() {
        String oldP = "password0";
        String newP = "password1";
        String repeat = "password2";
        assertThrows(ResponseStatusException.class, () -> PasswordValidator.validateEdit(oldP, newP, repeat));
    }
}
