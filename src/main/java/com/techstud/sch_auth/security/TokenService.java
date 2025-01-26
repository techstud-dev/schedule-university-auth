package com.techstud.sch_auth.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.dto.ServiceDto;
import com.techstud.sch_auth.entity.User;

public interface TokenService {

    String generateToken(User user, long expirationDate);
    String generateRefreshToken(User user, long expirationDate);
    String generateToken(ServiceDto info);
    String generateRefreshToken(ServiceDto info);
    String decodeIssuer(String token);
    DecodedJWT verifyToken(String token, Algorithm algorithm);
    Algorithm getAlgorithmForIssuer(String issuer);
}
