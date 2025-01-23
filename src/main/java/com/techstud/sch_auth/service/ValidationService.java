package com.techstud.sch_auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;

public interface ValidationService {

    void validateAndDecodeToken(String token, String expectedType);
    void validateRegister(RegisterDto registerDto);
    void validateLogin(LoginDto loginDto);
    void validateToken(DecodedJWT decodedJWT);
    void validateTokenType(DecodedJWT decodedJWT, String expectedType);
    void validateIssuer(DecodedJWT decodedJWT);
    String decodeIssuer(String token);
    Algorithm getAlgorithmForIssuer(String issuer);
    DecodedJWT verifyToken(String token, Algorithm algorithm);
}
