package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;

public interface LoginService {
    String processLogin(LoginDto loginDto);
}
