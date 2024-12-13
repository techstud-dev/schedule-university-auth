package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("AUTH_FACADE")
public class AuthFacade {

    private final LoginService loginService;
    private final RegistrationService registrationService;

    public AuthFacade(@Qualifier("LOGIN_SERVICE") LoginService loginService,
                      @Qualifier("REGISTRATION_SERVICE") RegistrationService registrationService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
    }

    public SuccessAuthenticationDto register(RegisterDto request) {
        return registrationService.processRegister(request);
    }

    public SuccessAuthenticationDto login(LoginDto request) {
        return loginService.processLogin(request);
    }

    public String refreshToken(String refreshToken) {
        // TODO: implement refresh token end-point
        return null;
    }
}
