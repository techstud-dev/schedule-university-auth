package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidJwtTokenException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Token expired or incorrect";

    public InvalidJwtTokenException() {
        super(STANDARD_MESSAGE);
    }
}
