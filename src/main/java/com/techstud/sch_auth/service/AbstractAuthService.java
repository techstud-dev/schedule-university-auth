package com.techstud.sch_auth.service;

import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@Data
public abstract class AbstractAuthService {

    protected final UserRepository userRepository;
    protected final RoleRepository roleRepository;
    protected final JwtGenerateService jwtGenerateService;

}
