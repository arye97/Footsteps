package com.springvuegradle.seng302team600.exception;

/**
 * Thrown when a maximum number of emails have already been reached
 * */
public class MaximumEmailsException extends Exception {
    public MaximumEmailsException() {
        super("Maximum email limit reached");
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
