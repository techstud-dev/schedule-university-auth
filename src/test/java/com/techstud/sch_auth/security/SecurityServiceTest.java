package com.techstud.sch_auth.security;

import com.techstud.sch_auth.dto.ServiceDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.security.impl.SecurityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processServiceAuthentication_Success() {
        String token = "test-token";
        String issuer = "test-issuer";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(tokenService.decodeIssuer(token)).thenReturn(issuer);
        when(tokenService.generateToken(any(ServiceDto.class))).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(any(ServiceDto.class))).thenReturn(refreshToken);

        SuccessAuthenticationDto result = securityService.processServiceAuthentication(token);

        assertNotNull(result, "Result should not be null");
        assertEquals(accessToken, result.getToken(), "Access token does not match");
        assertEquals(refreshToken, result.getRefreshToken(), "Refresh token does not match");
        verify(tokenService).decodeIssuer(token);
        verify(tokenService).generateToken(any(ServiceDto.class));
        verify(tokenService).generateRefreshToken(any(ServiceDto.class));
    }

    @Test
    void processServiceRefreshToken_Success() {
        String token = "test-token";
        String issuer = "test-issuer";
        String accessToken = "new-access-token";

        when(tokenService.decodeIssuer(token)).thenReturn(issuer);
        when(tokenService.generateToken(any(ServiceDto.class))).thenReturn(accessToken);

        String result = securityService.processServiceRefreshToken(token);

        assertNotNull(result, "Result should not be null");
        assertEquals(accessToken, result, "Access token does not match");
        verify(tokenService).decodeIssuer(token);
        verify(tokenService).generateToken(any(ServiceDto.class));
    }
}
