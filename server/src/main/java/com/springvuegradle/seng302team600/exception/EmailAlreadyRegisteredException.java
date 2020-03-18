package com.springvuegradle.seng302team600.exception;

/**
 * Thrown when a user tries to register an email that is already in the system
 * */
public class EmailAlreadyRegisteredException extends Exception {
    public EmailAlreadyRegisteredException() {
        // TODO Auto-generated constructor stub
    }

    public EmailAlreadyRegisteredException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public EmailAlreadyRegisteredException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public EmailAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public EmailAlreadyRegisteredException(String message, Throwable cause, boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
}
