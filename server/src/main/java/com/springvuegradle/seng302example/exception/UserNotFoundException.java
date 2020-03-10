package com.springvuegradle.seng302example.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String email) {
        super("Could not find user with email " + email);
    }
}
