package com.techstud.sch_auth.service.impl;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.exception.ValidationException;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final TokenService tokenService;

    @Override
    public void validateRegister(RegisterDto registerDto) {
        if (registerDto == null) {
            log.error("Register form is null");
            throw new ValidationException("Register form cannot be null.");
        }
        if (StringUtils.isBlank(registerDto.getEmail())) {
            log.error("Email is blank, request: {}", registerDto.getRequestId());
            throw new ValidationException("Email cannot be blank.");
        }
        if (!registerDto.getEmail().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
                || StringUtils.isBlank(registerDto.getEmail())) {
            log.error("Email format is invalid, request: {}", registerDto.getRequestId());
            throw new ValidationException("Email format is invalid.");
        }
        if (StringUtils.isBlank(registerDto.getPassword()) || registerDto.getPassword().length() < 8) {
            log.error("Password is invalid, request: {}", registerDto.getRequestId());
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (StringUtils.isBlank(registerDto.getUsername())) {
            log.error("Username is blank, request: {}", registerDto.getRequestId());
            throw new ValidationException("Username cannot be blank.");
        }
        if (StringUtils.isBlank(registerDto.getPhoneNumber())) {
            log.error("Phone number is invalid, request: {}", registerDto.getRequestId());
            throw new ValidationException("Phone number is invalid.");
        }
    }

    @Override
    public void validateLogin(LoginDto loginDto) {
        if (loginDto == null) {
            log.error("Login form is null");
            throw new ValidationException("Login form cannot be null.");
        }
        if (StringUtils.isBlank(loginDto.getIdentificationField())) {
            log.error("Identification field is blank");
            throw new ValidationException("Identification field cannot be blank.");
        }
        if (StringUtils.isBlank(loginDto.getPassword())) {
            log.error("Password is blank");
            throw new ValidationException("Password cannot be blank.");
        }
    }

    @Override
    public void validateAndDecodeToken(String token, String expectedType) {
        if (StringUtils.isBlank(token)) {
            log.error("Token is blank");
            throw new ValidationException("Token cannot be blank.");
        }
        String issuer = decodeIssuer(token);
        Algorithm algorithm = getAlgorithmForIssuer(issuer);
        DecodedJWT decodedJWT = verifyToken(token, algorithm);

        if (decodedJWT == null) {
            throw new InvalidJwtTokenException();
        }

        validateToken(decodedJWT);
        validateTokenType(decodedJWT, expectedType);
    }

    @Override
    public void validateToken(DecodedJWT decodedJWT) {
        if (decodedJWT.getExpiresAt() == null || decodedJWT.getExpiresAt().toInstant().isBefore(Instant.now())) {
            throw new InvalidJwtTokenException("Token has expired.");
        }
        validateIssuer(decodedJWT);
    }

    @Override
    public void validateTokenType(DecodedJWT decodedJWT, String expectedType) {
        String tokenType = decodedJWT.getClaim("type").asString();
        if (!expectedType.equals(tokenType)) {
            throw new InvalidJwtTokenException("Invalid token type. Expected: "
                    + expectedType + ", but found: " + tokenType);
        }
    }

    @Override
    public void validateIssuer(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            throw new InvalidJwtTokenException();
        }

        String issuer = decodedJWT.getIssuer();
        if (issuer == null || issuer.isEmpty()) {
            throw new InvalidJwtTokenException();
        }
    }

    @Override
    public String decodeIssuer(String token) {
        return tokenService.decodeIssuer(token);
    }

    @Override
    public Algorithm getAlgorithmForIssuer(String issuer) {
        return tokenService.getAlgorithmForIssuer(issuer);
    }

    @Override
    public DecodedJWT verifyToken(String token, Algorithm algorithm) {
        return tokenService.verifyToken(token, algorithm);
    }
}
