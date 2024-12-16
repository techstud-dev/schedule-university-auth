package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;

public interface RegistrationService {
    SuccessAuthenticationDto processRegister(RegisterDto registerDto);
}
