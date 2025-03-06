package com.techstud.sch_auth.dto;

import java.util.UUID;

public record LogoutRequest(String requestId, String refreshToken) {

    public static LogoutRequest generateFor(String refreshToken) {
        return new LogoutRequest(
                UUID.randomUUID().toString(),
                refreshToken
        );
    }
}
