package com.techstud.sch_auth.service;

import com.techstud.sch_auth.entity.RefreshToken;

public interface RefreshTokenService {
    String refreshToken(RefreshToken refreshToken);
}
