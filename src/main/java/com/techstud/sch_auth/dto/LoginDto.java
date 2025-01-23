package com.techstud.sch_auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginDto {

    private final String requestId;
    private final String identificationField;
    private final String password;

    public LoginDto(@NotNull String identificationField, @NotNull String password) {
        this.requestId = UUID.randomUUID().toString();
        this.identificationField = identificationField;
        this.password = password;
    }
}
