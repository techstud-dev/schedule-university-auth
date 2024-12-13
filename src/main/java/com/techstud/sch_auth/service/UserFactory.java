package com.techstud.sch_auth.service;

import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("USER_FACTORY")
public class UserFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(String username, String password, String email, String phoneNumber) {
        return User.builder()
                .username(username)
                .password(encryptPassword(password))
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
