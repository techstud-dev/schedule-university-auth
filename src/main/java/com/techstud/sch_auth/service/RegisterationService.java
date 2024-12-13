package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.RegisterDto;

public interface RegisterationService {
    String processRegister(RegisterDto registerDto);
}
