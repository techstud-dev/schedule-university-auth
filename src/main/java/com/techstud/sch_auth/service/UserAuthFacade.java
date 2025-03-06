package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.LogoutRequest;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthFacade {

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final RefreshTokenService refreshTokenService;
    private final LogoutService logoutService;
    private final ValidationService validationService;

    public SuccessAuthenticationDto register(RegisterDto request) {
        return registrationService.processRegister(request);
    }

    public SuccessAuthenticationDto login(LoginDto request) {
        return loginService.processLogin(request);
    }

    public String refreshToken(RefreshToken refreshToken) {
        validationService.validateAndDecodeToken(refreshToken.getRefreshToken(), "refresh");
        return refreshTokenService.refreshToken(refreshToken);
    }

    public void logout(LogoutRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new InvalidJwtTokenException("Refresh token is empty");
        }
        logoutService.clearRefreshToken(request);
    }
}
