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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service("REGISTRATION_SERVICE")
public class RegistrationServiceImpl extends AbstractAuthService implements RegistrationService {

    private final UserFactory userFactory;

    public RegistrationServiceImpl(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   JwtGenerateService jwtGenerateService,
                                   UserFactory userFactory) {
        super(userRepository, roleRepository, jwtGenerateService);
        this.userFactory = userFactory;
    }

    @Override
    public SuccessAuthenticationDto processRegister(RegisterDto registerDto) throws UserExistsException {
        validateUserUniqueness(registerDto);

        Role userRole = getOrCreateUserRole();
        User newUser = createUser(registerDto, userRole);

        String accessToken = jwtGenerateService.generateToken(newUser, 1);
        String refreshTokenString = jwtGenerateService.generateRefreshToken(newUser, 2);

        embedRefreshToken(newUser, refreshTokenString);
        userRepository.save(newUser);

        return buildSuccessResponse(accessToken, newUser.getRefreshToken());
    }

    private void validateUserUniqueness(RegisterDto registerDto) throws UserExistsException {
        if (userRepository.existsByUniqueFields(
                registerDto.getUsername(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber())) {
            throw new UserExistsException();
        }
    }

    private Role getOrCreateUserRole() {
        return roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            return roleRepository.save(newRole);
        });
    }

    private User createUser(RegisterDto registerDto, Role userRole) {
        User newUser = userFactory.createUser(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber()
        );
        newUser.setRole(userRole);
        return newUser;
    }

    private void embedRefreshToken(User user, String refreshTokenString) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        RefreshToken refreshToken = new RefreshToken(refreshTokenString, expiryDate);
        user.setRefreshToken(refreshToken);
    }

    private SuccessAuthenticationDto buildSuccessResponse(String accessToken, RefreshToken refreshToken) {
        SuccessAuthenticationDto response = new SuccessAuthenticationDto();
        response.setRequestId(UUID.randomUUID().toString());
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken.getRefreshToken());
        return response;
    }
}
