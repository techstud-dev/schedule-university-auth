package com.techstud.sch_auth.security;

import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityAuthFacade {

    private final SecurityService securityService;
    private final ValidationService validationService;

    public SuccessAuthenticationDto processAuthenticate(String token) {
        validationService.validateAndDecodeToken(token, "jwt");
        return securityService.processServiceAuthentication(token);
    }

    public String processRefreshToken(String refreshToken) {
        validationService.validateAndDecodeToken(refreshToken, "refresh");
        return securityService.processServiceRefreshToken(refreshToken);
    }
}
