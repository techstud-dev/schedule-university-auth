package com.techstud.sch_auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginDto {

    private String requestId = UUID.randomUUID().toString();
    private String identificationField;
    private String password;

}
