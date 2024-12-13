package com.techstud.sch_auth.dto;

import lombok.Data;

@Data
public class SuccessAuthenticationDto {

    private String requestId;
    private String token;
    private String refreshToken;

}
