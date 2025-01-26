package com.techstud.sch_auth.util;

import com.techstud.sch_auth.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }

    public List<ResponseCookie> createAuthCookies(String accessToken, String refreshToken) {
        return List.of(
                createHttpOnlyCookie("accessToken", accessToken, jwtProperties.getAccessTokenExpiration()),
                createHttpOnlyCookie("refreshToken", refreshToken, jwtProperties.getRefreshTokenExpiration())
        );
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return createHttpOnlyCookie("accessToken", accessToken, jwtProperties.getAccessTokenExpiration());
    }
}
