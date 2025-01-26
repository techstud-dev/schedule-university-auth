package com.techstud.sch_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginDto {

    private final String requestId;

    @NotBlank(message = "Identification field cannot be blank.")
    private final String identificationField;

    @NotBlank(message = "Password cannot be blank.")
    private final String password;

    public LoginDto(String identificationField, String password) {
        this.requestId = UUID.randomUUID().toString();
        this.identificationField = identificationField;
        this.password = password;
    }
}
