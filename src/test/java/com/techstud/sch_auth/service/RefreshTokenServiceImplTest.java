package com.techstud.sch_auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
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
    private JwtGenerateService jwtGenerateService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void testRefreshToken_Success() {
        RefreshToken refreshToken = new RefreshToken("validRefreshToken", LocalDateTime.now().plusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        User testUser = new User();
        testUser.setUsername("testUser");

        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq("refresh")))
                .thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("testUser");
        when(decodedJWT.getExpiresAt()).thenReturn(Date.from(Instant.now().plusSeconds(3600)));
        when(userRepository.findByUsernameIgnoreCase("testUser")).thenReturn(Optional.of(testUser));

        doReturn("newAccessToken").when(jwtGenerateService).generateToken(testUser, 1L);

        String newToken = refreshTokenService.refreshToken(refreshToken);

        assertEquals("newAccessToken", newToken);
    }

    @Test
    void testRefreshToken_TokenExpired() {
        RefreshToken refreshToken = new RefreshToken("expiredRefreshToken", LocalDateTime.now().minusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq("refresh")))
                .thenReturn(decodedJWT);
        when(decodedJWT.getExpiresAt()).thenReturn(Date.from(Instant.now().minusSeconds(3600)));

        assertThrows(InvalidJwtTokenException.class, () -> refreshTokenService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshToken_UserNotFound() {
        RefreshToken refreshToken = new RefreshToken("validRefreshToken", LocalDateTime.now().plusSeconds(3600));
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(jwtGenerateService.verifyToken(eq(refreshToken.getRefreshToken()), eq("refresh")))
                .thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("unknownUser");
        when(decodedJWT.getExpiresAt()).thenReturn(Date.from(Instant.now().plusSeconds(3600)));
        when(userRepository.findByUsernameIgnoreCase("unknownUser")).thenReturn(Optional.empty());

        assertThrows(InvalidJwtTokenException.class, () -> refreshTokenService.refreshToken(refreshToken));
    }
}
