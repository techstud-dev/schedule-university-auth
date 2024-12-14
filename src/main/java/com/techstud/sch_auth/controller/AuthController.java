package com.techstud.sch_auth.controller;

import com.techstud.sch_auth.config.JwtProperties;
import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.service.AuthFacade;
import com.techstud.sch_auth.swagger.BadCredentialsResponse;
import com.techstud.sch_auth.swagger.UserAlreadyExistResponse;
import com.techstud.sch_auth.swagger.UserNotFoundResponse;
import com.techstud.sch_auth.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

    @Operation(
            summary = "Логин существующего пользователя.",
            description = "Проверяет существует ли пользователь в системе и при успешном запросе возвращает JWT токены." +
                    " Файлы cookie для токенов устанавливаются в заголовках ответов.",
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные логина пользователяя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessAuthenticationDto.class)
                            ),
                            headers = {
                                    @Header(name = "Set-Cookie", description = "HttpOnly cookie содержит access token"),
                                    @Header(name = "Set-Cookie", description = "HttpOnly cookie содержит refresh token")
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверные входные данные",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadCredentialsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с такими данными не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserNotFoundResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        log.info("Received login request: {}", loginDto);
        SuccessAuthenticationDto response = authFacade.login(loginDto);

        ResponseCookie accessTokenCookie = cookieUtil.createHttpOnlyCookie("accessToken",
                response.getToken(), jwtProperties.getAccessTokenExpiration(), true);

        ResponseCookie refreshTokenCookie = cookieUtil.createHttpOnlyCookie("refreshToken",
                response.getRefreshToken(), jwtProperties.getRefreshTokenExpiration(), true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Регистрирует нового пользователя в системе и возвращает токены доступа и обновления JWT." +
                    " Файлы cookie для токенов устанавливаются в заголовках ответов.",
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные регистрации пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно зарегистрировался",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessAuthenticationDto.class)
                            ),
                            headers = {
                                    @Header(name = "Set-Cookie", description = "HttpOnly cookie содержит access token"),
                                    @Header(name = "Set-Cookie", description = "HttpOnly cookie содержит refresh token")
                            }
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Пользователь уже существует",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserAlreadyExistResponse.class)
                            )
                    )
            }
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
