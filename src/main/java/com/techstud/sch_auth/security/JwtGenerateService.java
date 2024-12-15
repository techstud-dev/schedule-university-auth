package com.techstud.sch_auth.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.User;

public interface JwtGenerateService {

    String generateToken(User user, long expirationDate);

    String generateRefreshToken(User user, long expirationDate);

    DecodedJWT verifyToken(String token, String expectedClaimType);

}
