package com.techstud.sch_auth.validation.impl;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.techstud.sch_auth.exception.BadCredentialsException;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final TokenService tokenService;

    @Override
    public void validateAndDecodeToken(String token, String expectedType) {
        if (StringUtils.isBlank(token)) {
            log.error("Token is blank");
            throw new BadCredentialsException("Token cannot be blank.");
        }
        String issuer = decodeIssuer(token);
        Algorithm algorithm = getAlgorithmForIssuer(issuer);
        DecodedJWT decodedJWT = verifyToken(token, algorithm);

        if (decodedJWT == null) {
            throw new InvalidJwtTokenException();
        }

        validateTokenType(decodedJWT, expectedType);

        validateToken(decodedJWT);
    }

    @Override
    public void validateToken(DecodedJWT decodedJWT) {
        if (decodedJWT.getExpiresAt() == null || decodedJWT.getExpiresAt().toInstant().isBefore(Instant.now())) {
            throw new InvalidJwtTokenException("Token has expired.");
        }
        validateIssuer(decodedJWT);
    }

    @Override
    public void validateTokenType(DecodedJWT decodedJWT, String expectedType) {
        String tokenType = decodedJWT.getClaim("type").asString();
        if (!expectedType.equals(tokenType)) {
            throw new InvalidJwtTokenException("Invalid token type. Expected: "
                    + expectedType + ", but found: " + tokenType);
        }
    }

    @Override
    public void validateIssuer(DecodedJWT decodedJWT) {
        if (decodedJWT == null) {
            throw new InvalidJwtTokenException();
        }

        String issuer = decodedJWT.getIssuer();
        if (issuer == null || issuer.isEmpty()) {
            throw new InvalidJwtTokenException();
        }
    }

    @Override
    public String decodeIssuer(String token) {
        return tokenService.decodeIssuer(token);
    }

    @Override
    public Algorithm getAlgorithmForIssuer(String issuer) {
        return tokenService.getAlgorithmForIssuer(issuer);
    }

    @Override
    public DecodedJWT verifyToken(String token, Algorithm algorithm) {
        return tokenService.verifyToken(token, algorithm);
    }
}
