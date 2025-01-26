package com.techstud.sch_auth.security.impl;

import com.techstud.sch_auth.dto.ServiceDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final TokenService tokenService;

    @Override
    public SuccessAuthenticationDto processServiceAuthentication(String token) {
        String issuer = tokenService.decodeIssuer(token);
        ServiceDto serviceInfo = new ServiceDto(issuer);

        String accessToken = tokenService.generateToken(serviceInfo);
        String refreshToken = tokenService.generateRefreshToken(serviceInfo);

        log.info("Service authenticated: {}", issuer);
        return new SuccessAuthenticationDto(accessToken, refreshToken);
    }

    @Override
    public String processServiceRefreshToken(String token) {
        String issuer = tokenService.decodeIssuer(token);
        ServiceDto serviceDto = new ServiceDto(issuer);

        return tokenService.generateToken(serviceDto);
    }
}
