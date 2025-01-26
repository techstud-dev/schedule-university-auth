package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.University;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UniversityRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.RegistrationService;
import com.techstud.sch_auth.exception.UserExistsException;
import com.techstud.sch_auth.service.UserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserFactory userFactory;
    private final TokenService tokenService;
    private final UniversityRepository universityRepository;

    @Override
    @Transactional
    public SuccessAuthenticationDto processRegister(RegisterDto registerDto) {
        validateUserUniqueness(registerDto);

        University university = resolveUniversity(registerDto.getUniversity());
        Role userRole = resolveRole("USER");

        User newUser = createAndSaveUser(registerDto, university, userRole);

        String accessToken = tokenService.generateToken(newUser, 15);
        String refreshToken = tokenService.generateRefreshToken(newUser, 1);

        newUser.setRefreshToken(new RefreshToken(refreshToken, Instant.now().plus(1, ChronoUnit.HOURS)));
        userRepository.save(newUser);

        log.info("User {} registered successfully", newUser.getUsername());
        return new SuccessAuthenticationDto(accessToken, refreshToken);
    }

    private University resolveUniversity(String universityName) {
        return universityRepository.findByName(universityName)
                .orElseGet(() -> universityRepository.save(new University(universityName)));
    }

    private Role resolveRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private User createAndSaveUser(RegisterDto registerDto, University university, Role role) {
        User newUser = userFactory.createUser(
                registerDto.getUsername(),
                registerDto.getFullName(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber(),
                registerDto.getGroupNumber()
        );
        newUser.setUniversity(university);
        newUser.setRoles(Set.of(role));
        return newUser;
    }

    private void validateUserUniqueness(RegisterDto registerDto) {
        if (userRepository.existsByUniqueFields(
                registerDto.getUsername(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber())) {
            throw new UserExistsException();
        }
    }
}
