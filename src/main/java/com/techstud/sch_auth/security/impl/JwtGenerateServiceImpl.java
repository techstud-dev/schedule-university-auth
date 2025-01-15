package com.techstud.sch_auth.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.Role;
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
                .withIssuer("auth")
                .withAudience("main")
                .withSubject(user.getUsername())
                .withClaim("type", "access")
                .withArrayClaim("roles", user.getRoles().stream()
                        .map(Role::getAuthority).toArray(String[]::new))
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationHours / 3600, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken(User user, long expirationHours) {
        return JWT.create()
                .withIssuer("auth")
                .withAudience("main")
                .withSubject(user.getUsername())
                .withClaim("type", "refresh")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationHours / 3600, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    @Override
    public String generateToken() {
        return JWT.create()
                .withIssuer("auth")
                .withAudience("services")
                .withClaim("type", "access")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken() {
        return JWT.create()
                .withIssuer("auth")
                .withAudience("services")
                .withClaim("type", "refresh")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))
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
            throw new InvalidJwtTokenException();
        }
    }
}
