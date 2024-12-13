package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;

public interface LoginService {
    SuccessAuthenticationDto processLogin(LoginDto loginDto);
}
