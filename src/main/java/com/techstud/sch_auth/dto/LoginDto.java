package com.techstud.sch_auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginDto {

    private final String requestId;
    private final String identificationField;
    private final String password;

    public LoginDto(String identificationField, String password) {
        this.requestId = UUID.randomUUID().toString();
        this.identificationField = identificationField;
        this.password = password;
    }
}
