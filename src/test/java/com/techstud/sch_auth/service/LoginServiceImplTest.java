package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.BadCredentialsException;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService jwtGenerateService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void processLogin_ShouldReturnSuccessResponse_WhenCredentialsAreValid() {
        LoginDto loginDto = new LoginDto("username", "password");
        User user = new User();
        user.setPassword("hashedPassword");
        RefreshToken refreshToken = new RefreshToken("refresh-token", Instant.now().plus(30, ChronoUnit.DAYS));
        user.setRefreshToken(refreshToken);

        when(userRepository.findByUsernameIgnoreCase("username"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword"))
                .thenReturn(true);
        when(jwtGenerateService.generateToken(user, 1))
                .thenReturn("access-token");
        when(jwtGenerateService.generateRefreshToken(user, 2))
                .thenReturn("new-refresh-token");

        SuccessAuthenticationDto response = loginService.processLogin(loginDto);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(userRepository).save(user);
    }

    @Test
    void processLogin_ShouldThrowException_WhenUserNotFound() {
        LoginDto loginDto = new LoginDto("nonexistent", "password");

        when(userRepository.findByUsernameIgnoreCase("nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loginService.processLogin(loginDto));

        verify(userRepository).findByUsernameIgnoreCase("nonexistent");
        verifyNoInteractions(jwtGenerateService, passwordEncoder);
    }

    @Test
    void processLogin_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        LoginDto loginDto = new LoginDto("username", "wrongPassword");
        User user = new User();
        user.setPassword("hashedPassword");

        when(userRepository.findByUsernameIgnoreCase("username"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> loginService.processLogin(loginDto));

        verify(passwordEncoder).matches("wrongPassword", "hashedPassword");
        verifyNoInteractions(jwtGenerateService);
    }

    @Test
    void processLogin_ShouldThrowException_WhenIdentificationFieldIsNullOrEmpty() {
        assertThrows(BadCredentialsException.class, () -> loginService.processLogin(new LoginDto(null, "password")));
        assertThrows(BadCredentialsException.class, () -> loginService.processLogin(new LoginDto(" ", "password")));
    }
}
