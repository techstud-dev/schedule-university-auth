package com.techstud.sch_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDto {

    private String requestId;

    @NotBlank(message = "Username cannot be blank.")
    private final String username;

    @NotBlank(message = "Full name cannot be null or empty.")
    private final String fullName;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private final String password;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email format is invalid.")
    private final String email;

    @NotBlank(message = "Phone number cannot be blank.")
    private final String phoneNumber;

    @NotBlank(message = "Group number cannot be blank.")
    private final String groupNumber;

    @NotBlank(message = "University cannot be blank.")
    private final String university;

    public RegisterDto(String username, String fullName, String password,
                       String email, String phoneNumber, String groupNumber,
                       String university) {
        this.requestId = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.groupNumber = groupNumber;
        this.university = university;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
