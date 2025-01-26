package com.techstud.sch_auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceImplTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ValidationServiceImpl validationService;

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

        InvalidJwtTokenException exception = assertThrows(
                InvalidJwtTokenException.class,
                () -> validationService.validateAndDecodeToken(token, expectedType)
        );

        assertEquals("Invalid token type. Expected: access, but found: refresh", exception.getMessage());
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
