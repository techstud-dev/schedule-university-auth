package com.techstud.sch_auth.controller;

import com.techstud.sch_auth.config.JwtProperties;
import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.service.AuthFacade;
import com.techstud.sch_auth.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProperties jwtProperties;
    private final AuthFacade authFacade;
    private final CookieUtil cookieUtil;

    public AuthController(@Qualifier("JWT_PROPERTIES") JwtProperties jwtProperties,
                          @Qualifier("AUTH_FACADE") AuthFacade authFacade,
                          @Qualifier("COOKIE_UTIL") CookieUtil cookieUtil) {
        this.jwtProperties = jwtProperties;
        this.authFacade = authFacade;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        //TODO: implement me!
        return ResponseEntity.ok().build();
    }

    @Operation(

    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        SuccessAuthenticationDto response = authFacade.register(registerDto);

        ResponseCookie accessTokenCookie = cookieUtil.createHttpOnlyCookie("accessToken",
                response.getToken(), jwtProperties.getAccessTokenExpiration(), true);

        ResponseCookie refreshTokenCookie = cookieUtil.createHttpOnlyCookie("refreshToken",
                response.getRefreshToken(), jwtProperties.getRefreshTokenExpiration(), true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }
}
