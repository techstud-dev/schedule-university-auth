package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LogoutRequest;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.service.impl.LogoutServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LogoutServiceImpl logoutService;

    @Test
    public void clearRefreshToken_WhenTokenExists_ShouldUpdateUser() {
        String validToken = "valid.token";
        when(userRepository.clearRefreshToken(validToken)).thenReturn(1);

        logoutService.clearRefreshToken(new LogoutRequest("req-123", validToken));

        verify(userRepository).clearRefreshToken(validToken);
    }

    @Test
    void clearRefreshToken_WhenTokenInvalid_ShouldThrowException() {
        String invalidToken = "invalid.token";
        when(userRepository.clearRefreshToken(invalidToken)).thenReturn(0);

        assertThrows(UserNotFoundException.class, () ->
                logoutService.clearRefreshToken(new LogoutRequest("req-456", invalidToken)));
    }
}
