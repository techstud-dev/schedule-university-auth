package com.techstud.sch_auth.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class RegisterDto {

    private final String requestId;
    private final String username;
    private final String password;
    private final String email;
    private final String phoneNumber;

    public RegisterDto(@NonNull String username, @NonNull String password,
                       @NonNull String email, @NonNull String phoneNumber) {
        this.requestId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
