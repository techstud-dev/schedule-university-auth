package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException {

    private static final String standardMessage = "Пользователь с такими данными уже существует";

    public UserExistsException() {
        super(standardMessage);
    }
}
