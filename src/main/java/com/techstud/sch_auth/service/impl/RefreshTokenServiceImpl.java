package com.techstud.sch_auth.service.impl;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public String refreshToken(RefreshToken refreshToken) {
        Algorithm algorithm = tokenService.getAlgorithmForIssuer("sch-auth");
        DecodedJWT decodedJWT = tokenService.verifyToken(refreshToken.getRefreshToken(), algorithm);

        String username = decodedJWT.getSubject();
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new InvalidJwtTokenException("User not found for refresh token."));

        log.info("Refreshing token for user: {}", username);
        return tokenService.generateToken(user, 1);
    }
}
