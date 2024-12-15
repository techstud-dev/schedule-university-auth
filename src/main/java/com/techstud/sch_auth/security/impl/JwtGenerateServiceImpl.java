package com.techstud.sch_auth.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
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
    public String generateToken(User user, long expirationHours) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getRole().getAuthority())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken(User user, long expirationDays) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationDays, ChronoUnit.DAYS)))
                .withClaim("type", "refresh")
                .sign(algorithm);
    }

    @Override
    public DecodedJWT verifyToken(String token, String expectedClaimType) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("type", expectedClaimType)
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("Invalid or expired token: {}", token, e);
            throw new InvalidJwtTokenException("Токен просрочен или неверен!");
        }
    }
}
