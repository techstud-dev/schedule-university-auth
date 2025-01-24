package com.techstud.sch_auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService jwtGenerateService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void testRefreshToken_Success() {
        Algorithm algorithm = Algorithm.HMAC256("test-secret");

        RefreshToken refreshToken = new RefreshToken("validRefreshToken", Instant.now().plusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        User testUser = new User();
        testUser.setUsername("testUser");

        when(jwtGenerateService.getAlgorithmForIssuer("sch-auth")).thenReturn(algorithm);
        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq(algorithm)))
                .thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("testUser");
        when(userRepository.findByUsernameIgnoreCase("testUser")).thenReturn(Optional.of(testUser));
        when(jwtGenerateService.generateToken(eq(testUser), eq(1L))).thenReturn("newAccessToken");

        String newToken = refreshTokenService.refreshToken(refreshToken);

        assertEquals("newAccessToken", newToken);

        verify(jwtGenerateService).getAlgorithmForIssuer("sch-auth");
        verify(jwtGenerateService).verifyToken(eq(refreshToken.getRefreshToken()), eq(algorithm));
        verify(decodedJWT).getSubject();
        verify(userRepository).findByUsernameIgnoreCase("testUser");
        verify(jwtGenerateService).generateToken(eq(testUser), eq(1L));
    }

    @Test
    void testRefreshToken_TokenExpired() {
        Algorithm algorithm = Algorithm.HMAC256("test-secret");
        when(jwtGenerateService.getAlgorithmForIssuer(anyString())).thenReturn(algorithm);

        RefreshToken refreshToken = new RefreshToken("expiredRefreshToken", Instant.now().minusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq(algorithm)))
                .thenReturn(decodedJWT);

        assertThrows(InvalidJwtTokenException.class, () -> refreshTokenService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshToken_UserNotFound() {
        Algorithm algorithm = Algorithm.HMAC256("test-secret");
        when(jwtGenerateService.getAlgorithmForIssuer("sch-auth")).thenReturn(algorithm);

        RefreshToken refreshToken = new RefreshToken("validRefreshToken", Instant.now().plusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq(algorithm)))
                .thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("unknownUser");
        when(userRepository.findByUsernameIgnoreCase("unknownUser")).thenReturn(Optional.empty());

        assertThrows(InvalidJwtTokenException.class, () -> refreshTokenService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshToken_InvalidTokenFormat() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(null);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));

        assertThrows(InvalidJwtTokenException.class, () -> refreshTokenService.refreshToken(refreshToken));
    }
}
