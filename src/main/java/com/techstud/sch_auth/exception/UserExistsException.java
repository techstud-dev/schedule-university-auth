package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "User with these credentials already exists";

    public UserExistsException() {
        super(STANDARD_MESSAGE);
    }
}
