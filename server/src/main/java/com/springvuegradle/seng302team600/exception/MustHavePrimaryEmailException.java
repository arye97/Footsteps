package com.springvuegradle.seng302team600.exception;

public class MustHavePrimaryEmailException extends Exception {
    public MustHavePrimaryEmailException(String email, String message) {
        super("Cannot make " + email + message);
    }
}
