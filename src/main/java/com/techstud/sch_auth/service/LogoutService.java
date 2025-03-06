package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LogoutRequest;

public interface LogoutService {
    void clearRefreshToken(LogoutRequest request);
}
