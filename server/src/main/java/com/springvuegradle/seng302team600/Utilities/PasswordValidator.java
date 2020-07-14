package com.springvuegradle.seng302team600.Utilities;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasswordValidator {

    private static final String PASSWORD_RULES_REGEX = "(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}";

    /**
     * Checks the given password against the password rules. If invalid throws ResponseStatusException
     * Should be used when creating a new user.
     * @param password the password to check rule with
     * @return true if password follows the rules, otherwise throw ResponseStatusException
     */
    public static boolean validate(String password) {
        validateRegEx(password);
        return true;
    }

    /**
     * Checks the given password against the password rules. Also checks old, new and repeated passwords
     * against each other.
     * If invalid throws ResponseStatusException
     * Should be used when editing a user.
     * @param oldPassword the current password
     * @param newPassword the password to be added
     * @param repeatPassword the repeated password
     * @return true if passwords follow the rules, otherwise throw ResponseStatusException
     */
    public static boolean validateEdit(String oldPassword, String newPassword, String repeatPassword) {
        validateRegEx(newPassword);
        validateOldPasswordWithNew(oldPassword, newPassword);
        validateNewPasswordWithRepeat(newPassword, repeatPassword);
        return true;
    }

    /**
     * Checks is a password complies with the password rules.
     * Password has to be longer than 8 characters and have at least one digit.
     * @param password the password to check
     */
    private static void validateRegEx(String password) {
        if (!password.matches(PASSWORD_RULES_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password does not follow password rules");
        }
    }

    /**
     * Invalid if new password matches old password, throws ResponseStatusException
     * @param oldPassword the current password
     * @param newPassword the password to be added
     */
    private static void validateOldPasswordWithNew(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password equals current password");
        }
    }

    /**
     * Invalid if new password does not equal repeated password, throws ResponseStatusException
     * @param newPassword the password to be added
     * @param repeatPassword the repeated password
     */
    private static void validateNewPasswordWithRepeat(String newPassword, String repeatPassword) {
        if (!newPassword.equals(repeatPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must match repeated password");
        }
    }
}
