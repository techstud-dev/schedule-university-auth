package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.AbstractAuthService;
import com.techstud.sch_auth.service.RegistrationService;
import com.techstud.sch_auth.exception.UserExistsException;
import com.techstud.sch_auth.service.UserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service("REGISTRATION_SERVICE")
public class RegistrationServiceImpl extends AbstractAuthService implements RegistrationService {

    private final UserFactory userFactory;

    public RegistrationServiceImpl(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   JwtGenerateService jwtGenerateService,
                                   UserFactory userFactory1) {
        super(userRepository, roleRepository, jwtGenerateService);
        this.userFactory = userFactory1;
    }

    @Override
    public SuccessAuthenticationDto processRegister(RegisterDto registerDto) throws UserExistsException {

        if (userRepository.existsByUniqueFields(
                registerDto.getUsername(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber())) {
            throw new UserExistsException("User with these credentials already exists!");
        }

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            return roleRepository.save(newRole);
        });

        User newUser = userFactory.createUser(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber()
        );

        newUser.setRole(userRole);

        String accessToken = jwtGenerateService.generateToken(newUser);
        String refreshToken = jwtGenerateService.generateRefreshToken(newUser);

        RefreshToken embeddedRefreshToken = new RefreshToken();
        embeddedRefreshToken.setRefreshToken(refreshToken);
        embeddedRefreshToken.setExpiryDate(LocalDateTime.now().plusHours(2));
        newUser.setRefreshToken(embeddedRefreshToken);

        userRepository.save(newUser);

        log.info("Saved new user: {}", newUser);

        SuccessAuthenticationDto response = new SuccessAuthenticationDto();
        response.setRequestId(UUID.randomUUID().toString());
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }
}
