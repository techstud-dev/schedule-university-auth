package com.techstud.sch_auth.controller;

import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.security.SecurityAuthFacade;
import com.techstud.sch_auth.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/service/auth")
@RequiredArgsConstructor
public class ServiceAuthController {

    private final SecurityAuthFacade securityFacade;
    private final ResponseUtil responseUtil;

    @PostMapping("/validate-service")
    public ResponseEntity<Void> validateAndAuthenticateService(@RequestHeader("Authorization") String token) {
        SuccessAuthenticationDto tokens = securityFacade.processAuthenticate(token);

        Map<String, String> headers = Map.of(
                "Access-Token", tokens.getToken(),
                "Refresh-Token", tokens.getRefreshToken()
        );

        return responseUtil.okWithHeadersOnly(headers);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> refreshAccessServiceToken(@RequestHeader("Authorization") String token) {
        String accessToken = securityFacade.processRefreshToken(token);

        Map<String, String> headers = Map.of("Access-Token", accessToken);
        return responseUtil.okWithHeadersOnly(headers);
    }
}
