package com.springvuegradle.seng302team600.exception;

public class MustHavePrimaryEmailException extends Exception {
    public MustHavePrimaryEmailException() {
        super("Primary email must be created before additional emails are added");
    }
}
