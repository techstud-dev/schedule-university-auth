package com.techstud.sch_auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.exception.ValidationException;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValidationServiceImplTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ValidationServiceImpl validationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateRegister_Success() {
        RegisterDto registerDto = new RegisterDto("username", "password123",
                "user@example.com", "324134153415");

        assertDoesNotThrow(() -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateRegister_Failure_NullDto() {
        assertThrows(ValidationException.class, () -> validationService.validateRegister(null));
    }

    @Test
    void validateRegister_Failure_BlankEmail() {
        RegisterDto registerDto = new RegisterDto("username", "password123",
                " ", "324134153415");

        assertThrows(ValidationException.class, () -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateRegister_Failure_InvalidEmailFormat() {
        RegisterDto registerDto = new RegisterDto("username", "password123",
                "invalid-email", "324134153415");

        assertThrows(ValidationException.class, () -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateRegister_Failure_ShortPassword() {
        RegisterDto registerDto = new RegisterDto("username", "short",
                "user@example.com", "324134153415");

        assertThrows(ValidationException.class, () -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateRegister_Failure_BlankUsername() {
        RegisterDto registerDto = new RegisterDto(" ", "password123",
                "user@example.com", "324134153415");

        assertThrows(ValidationException.class, () -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateRegister_Failure_BlankPhoneNumber() {
        RegisterDto registerDto = new RegisterDto("username", "password123",
                "user@example.com", " ");

        assertThrows(ValidationException.class, () -> validationService.validateRegister(registerDto));
    }

    @Test
    void validateLogin_Success() {
        LoginDto loginDto = new LoginDto("user@example.com", "password123");

        assertDoesNotThrow(() -> validationService.validateLogin(loginDto));
    }

    @Test
    void validateLogin_Failure_NullDto() {
        assertThrows(ValidationException.class, () -> validationService.validateLogin(null));
    }

    @Test
    void validateLogin_Failure_BlankIdentificationField() {
        LoginDto loginDto = new LoginDto(" ", "password123");

        assertThrows(ValidationException.class, () -> validationService.validateLogin(loginDto));
    }

    @Test
    void validateLogin_Failure_BlankPassword() {
        LoginDto loginDto = new LoginDto("user@example.com", " ");

        assertThrows(ValidationException.class, () -> validationService.validateLogin(loginDto));
    }

    @Test
    void validateAndDecodeToken_Failure_BlankToken() {
        assertThrows(ValidationException.class, () -> validationService.validateAndDecodeToken(" ", "access"));
    }

    @Test
    void validateAndDecodeToken_Failure_InvalidTokenType() {
        String token = "valid-token";
        String expectedType = "access";

        when(tokenService.decodeIssuer(token)).thenReturn("valid-issuer");

        Algorithm algorithm = Algorithm.HMAC256("secret");
        when(tokenService.getAlgorithmForIssuer("valid-issuer")).thenReturn(algorithm);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(tokenService.verifyToken(token, algorithm)).thenReturn(decodedJWT);

        Claim typeClaim = mock(Claim.class);
        when(decodedJWT.getClaim("type")).thenReturn(typeClaim);
        when(typeClaim.asString()).thenReturn("refresh");

        when(decodedJWT.getExpiresAt()).thenReturn(Date.from(Instant.now().plusSeconds(3600)));

        assertThrows(InvalidJwtTokenException.class,
                () -> validationService.validateAndDecodeToken(token, expectedType));
    }

    @Test
    void validateToken_Failure_ExpiredToken() {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getExpiresAt()).thenReturn(Date.from(Instant.now().minusSeconds(3600)));

        assertThrows(InvalidJwtTokenException.class, () -> validationService.validateToken(decodedJWT));
    }

    @Test
    void validateIssuer_Failure_NullIssuer() {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getIssuer()).thenReturn(null);

        assertThrows(InvalidJwtTokenException.class, () -> validationService.validateIssuer(decodedJWT));
    }
}
