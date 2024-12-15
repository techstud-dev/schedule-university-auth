package com.techstud.sch_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String requestId = UUID.randomUUID().toString();
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    public RegisterDto(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
