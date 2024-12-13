package com.techstud.sch_auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDto {

    private String requestId = UUID.randomUUID().toString();
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

}
