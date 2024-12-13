package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.AbstractAuthService;
import com.techstud.sch_auth.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LOGIN_SERVICE")
public class LoginServiceImpl extends AbstractAuthService implements LoginService {

    public LoginServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            JwtGenerateService jwtGenerateService) {
        super(userRepository, roleRepository, jwtGenerateService);
    }

    @Override
    public SuccessAuthenticationDto processLogin(LoginDto loginDto) {
        return null;
    }
}
