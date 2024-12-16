package com.techstud.sch_auth.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds, boolean isSecure) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }
}
