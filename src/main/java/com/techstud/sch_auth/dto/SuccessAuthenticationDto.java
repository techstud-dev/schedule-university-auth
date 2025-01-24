package com.techstud.sch_auth.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class SuccessAuthenticationDto {

    private final String requestId;
    private final String token;
    private final String refreshToken;

    public SuccessAuthenticationDto(String token, String refreshToken) {
        this.requestId = UUID.randomUUID().toString();
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
