package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;

@StandardException
public class InvalidJwtTokenException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Token expired or incorrect";

    public InvalidJwtTokenException() {
        super(STANDARD_MESSAGE);
    }
}
