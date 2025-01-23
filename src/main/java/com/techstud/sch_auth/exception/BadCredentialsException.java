package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;

@StandardException
public class BadCredentialsException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Incorrect credentials";

    public BadCredentialsException() {
        super(STANDARD_MESSAGE);
    }
}
