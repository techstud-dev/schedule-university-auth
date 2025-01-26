package com.techstud.sch_auth.security;

import com.techstud.sch_auth.dto.SuccessAuthenticationDto;

public interface SecurityService {

    SuccessAuthenticationDto processServiceAuthentication(String token);
    String processServiceRefreshToken(String token);
}
