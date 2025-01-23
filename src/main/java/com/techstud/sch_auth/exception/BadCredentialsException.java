package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadCredentialsException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Incorrect credentials";

    public BadCredentialsException() {
        super(STANDARD_MESSAGE);
    }
}
