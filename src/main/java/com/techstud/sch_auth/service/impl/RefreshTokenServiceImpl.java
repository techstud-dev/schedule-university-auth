package com.techstud.sch_auth.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.AbstractAuthService;
import com.techstud.sch_auth.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class RefreshTokenServiceImpl extends AbstractAuthService implements RefreshTokenService {

    public RefreshTokenServiceImpl(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   JwtGenerateService jwtGenerateService) {
        super(userRepository, roleRepository, jwtGenerateService);
    }

    @Override
    public String refreshToken(RefreshToken refreshToken) {
        DecodedJWT decodedJWT = jwtGenerateService
                .verifyToken(refreshToken.getRefreshToken(), "refresh");

        String username = decodedJWT.getSubject();
        Instant tokenExpiry = decodedJWT.getExpiresAt().toInstant();

        if (tokenExpiry.isBefore(Instant.now())) {
            throw new InvalidJwtTokenException();
        }
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error("Failed to find user with username" +
                            " extracted from the refresh token: {}", username);
                    return new InvalidJwtTokenException();
                });

        return jwtGenerateService.generateToken(user, 1);
    }
}
