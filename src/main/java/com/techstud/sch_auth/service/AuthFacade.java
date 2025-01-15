package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.security.JwtGenerateService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtGenerateService jwtGenerateService;

    public AuthFacade(LoginService loginService,
                      RegistrationService registrationService,
                      RefreshTokenService refreshTokenService,
                      JwtGenerateService jwtGenerateService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtGenerateService = jwtGenerateService;
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

    public String generateRefreshTokenSimple() {
        return jwtGenerateService.generateRefreshToken();
    }

    public String generateAccessTokenSimple() {
        return jwtGenerateService.generateToken();
    }
}
