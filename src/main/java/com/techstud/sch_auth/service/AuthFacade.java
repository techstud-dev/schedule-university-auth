package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("AUTH_FACADE")
public class AuthFacade {

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final RefreshTokenService refreshTokenService;

    public AuthFacade(@Qualifier("LOGIN_SERVICE") LoginService loginService,
                      @Qualifier("REGISTRATION_SERVICE") RegistrationService registrationService,
                      @Qualifier("REFRESH-TOKEN-SERVICE") RefreshTokenService refreshTokenService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.refreshTokenService = refreshTokenService;
    }

    public SuccessAuthenticationDto register(RegisterDto request) {
        return registrationService.processRegister(request);
    }

    public SuccessAuthenticationDto login(LoginDto request) {
        return loginService.processLogin(request);
    }

    public String refreshToken(RefreshToken refreshToken) {
        return refreshTokenService.refreshToken(refreshToken);
    }
}
