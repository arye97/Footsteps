package com.springvuegradle.seng302team600.exception;

public class IncorrectPasswordException extends Exception {
    public IncorrectPasswordException(String email) {
        super("Login with " + email + " unsuccessful, please enter a valid password");
    }
}
