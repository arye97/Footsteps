package com.springvuegradle.seng302team600.advice;

import com.springvuegradle.seng302team600.exception.EmailAlreadyRegisteredException;
import com.springvuegradle.seng302team600.exception.IncorrectPasswordException;
import com.springvuegradle.seng302team600.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Advices handle the exceptions which are called. They can force Http status codes
 * and return the error message without resulting in INTERNAL_SERVER_ERROR status codes
 */
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String emailAlreadyRegisteredHandler(EmailAlreadyRegisteredException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String MethodArgumentNotValidExceptionHandler(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundHandler(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String incorrectPasswordHandler(IncorrectPasswordException ex) {
        return ex.getMessage();
    }
}
