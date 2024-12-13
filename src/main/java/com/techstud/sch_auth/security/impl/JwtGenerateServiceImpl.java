package com.techstud.sch_auth.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.security.JwtGenerateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtGenerateServiceImpl implements JwtGenerateService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        if (SECRET_KEY != null && !SECRET_KEY.isEmpty()) {
            this.algorithm = Algorithm.HMAC256(SECRET_KEY);
        } else {
            throw new IllegalArgumentException("The Secret cannot be null or empty");
        }
    }

    @Override
    public String generateToken(User user) {
        long ACCESS_TOKEN_EXPIRATION_HOURS = 1;
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(java.util.Date.from(Instant.now().plus(ACCESS_TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken(User user) {
        long REFRESH_TOKEN_EXPIRATION_HOURS = 2;
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(java.util.Date.from(Instant.now().plus(REFRESH_TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS)))
                .sign(algorithm);
    }
}
