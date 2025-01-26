package com.techstud.sch_auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public interface ValidationService {

    void validateAndDecodeToken(String token, String expectedType);
    void validateToken(DecodedJWT decodedJWT);
    void validateTokenType(DecodedJWT decodedJWT, String expectedType);
    void validateIssuer(DecodedJWT decodedJWT);
    String decodeIssuer(String token);
    Algorithm getAlgorithmForIssuer(String issuer);
    DecodedJWT verifyToken(String token, Algorithm algorithm);
}
