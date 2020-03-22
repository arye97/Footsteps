package com.springvuegradle.seng302team600.exception;

public class InvalidUserNameException extends Exception {
    public InvalidUserNameException() { super("Invalid name. Name must contain at least one letter and no non-letter characters"); }
}