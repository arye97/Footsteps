package com.springvuegradle.seng302example.exception;

/**
 * Thrown when a maximum number of emails have already been reached
 * */
public class MaximumEmailsException extends Exception {
    public MaximumEmailsException() {
        // TODO Auto-generated constructor stub
    }

    public MaximumEmailsException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public MaximumEmailsException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public MaximumEmailsException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public MaximumEmailsException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
}
