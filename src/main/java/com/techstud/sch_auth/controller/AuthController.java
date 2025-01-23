package com.techstud.sch_auth.controller;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.service.UserAuthFacade;
import com.techstud.sch_auth.swagger.BadCredentialsResponse;
import com.techstud.sch_auth.swagger.InvalidJwtTokenResponse;
import com.techstud.sch_auth.swagger.UserAlreadyExistResponse;
import com.techstud.sch_auth.swagger.UserNotFoundResponse;
import com.techstud.sch_auth.util.CookieUtil;
import com.techstud.sch_auth.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthFacade authFacade;
    private final CookieUtil cookieUtil;
    private final ResponseUtil responseUtil;

    @Operation(
            summary = "Логин существующего пользователя",
            description = """
            Аутентифицирует пользователя на основе его учетных данных (username, email или phoneNumber).
            При успешной аутентификации возвращает access и refresh JWT токены.
            Токены устанавливаются в HttpOnly cookie в заголовках ответа.
            """,
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Данные для входа пользователя.
                Поле `identificationField` может содержать username, email или номер телефона.
                Пример запроса:
                {
                    "identificationField": "user@example.com",
                    "password": "secure_password"
                }
                """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessAuthenticationDto.class)
                            ),
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie содержит access token.",
                                            schema = @Schema(type = "string")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie содержит refresh token.",
                                            schema = @Schema(type = "string")
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверные учетные данные.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadCredentialsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с указанными учетными данными не найден.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserNotFoundResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<SuccessAuthenticationDto> login(@RequestBody LoginDto loginDto) {
        SuccessAuthenticationDto response = authFacade.login(loginDto);

        List<ResponseCookie> cookies = cookieUtil.createAuthCookies(
                response.getToken(),
                response.getRefreshToken()
        );

        return responseUtil.okWithCookies(response, cookies.toArray(ResponseCookie[]::new));
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = """
            Регистрирует нового пользователя в системе.
            После успешной регистрации возвращает JWT токены: access token и refresh token.
            Токены передаются в ответе в виде HttpOnly cookie.
            """,
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Данные для регистрации нового пользователя.
                Объект должен содержать уникальные значения для `username`, `email` и `phoneNumber`.
                Пример запроса:
                {
                    "username": "new_user",
                    "password": "secure_password",
                    "email": "user@example.com",
                    "phoneNumber": "+1234567890"
                }
                """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно зарегистрировался.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessAuthenticationDto.class)
                            ),
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie содержит access token.",
                                            schema = @Schema(type = "string")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie содержит refresh token.",
                                            schema = @Schema(type = "string")
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = """
                    Пользователь с указанными данными уже существует.
                    Проверяются уникальные поля: `username`, `email` и `phoneNumber`.
                    """,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserAlreadyExistResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные учетные данные.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadCredentialsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден (неактуально для регистрации, но можно оставить как общий ответ).",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserNotFoundResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<SuccessAuthenticationDto> register(@RequestBody RegisterDto registerDto) {
        SuccessAuthenticationDto response = authFacade.register(registerDto);

        List<ResponseCookie> cookies = cookieUtil.createAuthCookies(
                response.getToken(),
                response.getRefreshToken()
        );

        return responseUtil.okWithCookies(response, cookies.toArray(ResponseCookie[]::new));
    }

    @Operation(
            summary = "Обновление access токена",
            description = """
        Использует переданный refresh токен для обновления access токена.
        Новый access токен генерируется на основе refresh токена.
        Новый access токен возвращается в виде HttpOnly cookie в заголовке ответа.
        """,
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
            Объект с refresh токеном, который используется для обновления access токена.
            Пример запроса:
            {
                "refreshToken": "your_refresh_token"
            }
            """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                    {
                        "refreshToken": "your_refresh_token"
                    }
                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access токен успешно обновлён.",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "HttpOnly cookie содержит новый access токен.",
                                            schema = @Schema(type = "string")
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "Successfully refreshed token")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = """
                Обновление токена не удалось. Возможные причины:
                - Refresh токен недействителен
                - Refresh токен истёк
                - Пользователь не найден
                """,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvalidJwtTokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"Internal Server Error\"}")
                            )
                    )
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshToken refreshTokenRequest) {
        String accessToken = authFacade.refreshToken(refreshTokenRequest);

        ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);

        return responseUtil.okWithCookies(accessToken, accessTokenCookie);
    }

}
