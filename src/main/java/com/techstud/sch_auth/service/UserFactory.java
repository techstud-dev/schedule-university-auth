package com.techstud.sch_auth.service;

import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.BadCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(String username, String fullName, String password, String email,
                           String phoneNumber, String groupNumber) {
        if (username == null || fullName == null || password == null || email == null
                || phoneNumber == null || groupNumber == null) {
            throw new BadCredentialsException();
        }
        return User.builder()
                .username(username)
                .fullName(fullName)
                .password(encryptPassword(password))
                .email(email)
                .phoneNumber(phoneNumber)
                .groupNumber(groupNumber)
                .build();
    }

    private String encryptPassword(String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        if (encryptedPassword == null) {
            throw new IllegalStateException("Password encryption failed");
        }
        return encryptedPassword;
    }
}
