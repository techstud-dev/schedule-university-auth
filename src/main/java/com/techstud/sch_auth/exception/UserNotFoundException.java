package com.techstud.sch_auth.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private static final String standardMessage = "Пользователь с такими данными не найден";

    public UserNotFoundException() {
        super(standardMessage);
    }
}
