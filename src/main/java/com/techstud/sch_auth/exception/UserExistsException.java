package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;

@StandardException
public class UserExistsException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "User with these credentials already exists";

    public UserExistsException() {
        super(STANDARD_MESSAGE);
    }
}
