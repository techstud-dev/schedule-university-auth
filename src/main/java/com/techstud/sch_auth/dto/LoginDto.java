package com.techstud.sch_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String requestId = UUID.randomUUID().toString();
    private String identificationField;
    private String password;

    public LoginDto(String testUser, String password) {
        this.identificationField = testUser;
        this.password = password;
    }
}
