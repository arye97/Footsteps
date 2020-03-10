package com.springvuegradle.seng302team600.advice;

import com.springvuegradle.seng302team600.exception.EmailAlreadyRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EmailAlreadyRegisteredAdvice {

    @ResponseBody
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String emailAlreadyRegisteredHandler(EmailAlreadyRegisteredException ex) {
        return ex.getMessage();
    }
}
