package com.springvuegradle.seng302team600.exception;

public class UserTooYoungException extends Exception {
    public UserTooYoungException() { super("You must be at least 13 years old to register for this app"); }
}
