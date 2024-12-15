package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidJwtTokenException extends RuntimeException {

    private static final String standardMessage = "Токен просрочен или неверен!";

    public InvalidJwtTokenException() {
        super(standardMessage);
    }
}
