package com.techstud.sch_auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SuccessAuthenticationDto {

    private String requestId;
    private String token;
    private String refreshToken;

    public SuccessAuthenticationDto(String token, String refreshToken) {
        this.requestId = UUID.randomUUID().toString();
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
