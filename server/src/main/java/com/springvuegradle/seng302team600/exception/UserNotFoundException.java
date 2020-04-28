package com.springvuegradle.seng302team600.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String email) {
        super("Could not find user with email " + email);
    }
    public UserNotFoundException(Long id) {
        super("Could not find user with id " + id);
    }
}
