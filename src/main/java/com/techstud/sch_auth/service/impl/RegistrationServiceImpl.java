package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.AbstractAuthService;
import com.techstud.sch_auth.service.RegisterationService;
import exception.UserExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RegistrationServiceImpl extends AbstractAuthService implements RegisterationService {

    public RegistrationServiceImpl(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   JwtGenerateService jwtGenerateService) {
        super(userRepository, roleRepository, jwtGenerateService);
    }

    @Override
    public String processRegister(RegisterDto registerDto) throws UserExistsException {

        if (userRepository.existsByUniqueFields(
                registerDto.getUsername(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber())) {
            throw new UserExistsException("User with this username or email or phoneNumber already exists!");
        }

        return null;
    }
}
