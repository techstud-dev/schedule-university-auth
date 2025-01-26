package com.techstud.sch_auth.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.dto.ServiceDto;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.security.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.parser-secret}")
    private String PARSER_SECRET_KEY;

    @Value("${jwt.main-secret}")
    private String MAIN_SECRET_KEY;

    @Value("${jwt.auth-issuer}")
    private String authIssuer;

    @Value("${jwt.main-audience}")
    private String mainAudience;

    private Algorithm authAlgorithm;
    private Algorithm mainAlgorithm;
    private Algorithm parserAlgorithm;

    @PostConstruct
    public void initAlgorithms() {
        log.debug("Initializing algorithms...");
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("The Secret cannot be null or empty for authAlgorithm");
        }
        if (MAIN_SECRET_KEY == null || MAIN_SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("The Secret cannot be null or empty for mainAlgorithm");
        }
        if (PARSER_SECRET_KEY == null || PARSER_SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("The Secret cannot be null or empty for parserAlgorithm");
        }

        this.authAlgorithm = Algorithm.HMAC256(SECRET_KEY);
        this.mainAlgorithm = Algorithm.HMAC256(MAIN_SECRET_KEY);
        this.parserAlgorithm = Algorithm.HMAC256(PARSER_SECRET_KEY);
    }

    @Override
    public String generateToken(User user, long expirationMinutes) {
        log.info("Generating access token for: {}", user.getUsername());
        return JWT.create()
                .withIssuer(authIssuer)
                .withAudience(mainAudience)
                .withSubject(user.getUsername())
                .withClaim("type", "access")
                .withArrayClaim("roles", user.getRoles().stream()
                        .map(Role::getAuthority).toArray(String[]::new))
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .sign(authAlgorithm);
    }

    @Override
    public String generateRefreshToken(User user, long expirationHours) {
        log.info("Generating refresh token for: {}", user.getUsername());
        return JWT.create()
                .withIssuer(authIssuer)
                .withAudience(mainAudience)
                .withSubject(user.getUsername())
                .withClaim("type", "refresh")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
                .sign(authAlgorithm);
    }

    @Override
    public String generateToken(ServiceDto info) {
        log.info("Generating access token for service: {}, " +
                "request: {}", info.getName(), info.getRequestId());
        return JWT.create()
                .withIssuer(authIssuer)
                .withAudience(info.getName())
                .withClaim("type", "access")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .sign(authAlgorithm);
    }

    @Override
    public String generateRefreshToken(ServiceDto info) {
        log.info("Generating refresh token for service: {}, " +
                "request: {}", info.getName(), info.getRequestId());
        String uniqueIssuer = authIssuer + "-" + info.getName();
        return JWT.create()
                .withIssuer(uniqueIssuer)
                .withAudience(info.getName())
                .withClaim("type", "refresh")
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .sign(authAlgorithm);
    }

    @Override
    public DecodedJWT verifyToken(String token, Algorithm algorithm) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length()).trim();
            }

            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("Token verification failed: {}", e.getMessage());
            throw new InvalidJwtTokenException("Token verification failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String decodeIssuer(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length()).trim();
            }

            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getIssuer();
        } catch (JWTDecodeException e) {
            log.error("Failed to decode token issuer: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to decode token issuer: " + e.getMessage(), e);
        }
    }

    @Override
    public Algorithm getAlgorithmForIssuer(String issuer) {
        log.info("Getting algorithm for issuer: {}", issuer);
        return switch (issuer) {
            case "sch-parser" -> parserAlgorithm;
            case "sch-auth", "sch-auth-sch-parser", "sch-auth-sch-main" -> authAlgorithm;
            case "sch-main" -> mainAlgorithm;
            default -> {
                log.info("Get algorithm failed. Provided issuer: {}", issuer);
                throw new IllegalArgumentException("Unknown issuer: " + issuer);
            }
        };
    }
}
