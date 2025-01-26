package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.University;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.UserExistsException;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UniversityRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.impl.RegistrationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterDto registerDto = new RegisterDto(
                "username",
                "Full Name",
                "Password123",
                "email@example.com",
                "1234567890",
                "Group-1",
                "Some University"
        );

        University university = new University("Some University");
        university.setId(1L);

        Role userRole = new Role("USER");
        userRole.setId(1L);

        User user = User.builder()
                .username(registerDto.getUsername())
                .fullName(registerDto.getFullName())
                .password("EncryptedPassword123")
                .email(registerDto.getEmail())
                .phoneNumber(registerDto.getPhoneNumber())
                .groupNumber(registerDto.getGroupNumber())
                .university(university)
                .roles(Set.of(userRole))
                .build();

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(universityRepository.findByName(registerDto.getUniversity()))
                .thenReturn(Optional.of(university));
        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(userRole));
        when(userFactory.createUser(
                registerDto.getUsername(),
                registerDto.getFullName(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber(),
                registerDto.getGroupNumber()
        )).thenReturn(user);
        when(tokenService.generateToken(user, 15))
                .thenReturn(accessToken);
        when(tokenService.generateRefreshToken(user, 1))
                .thenReturn(refreshToken);

        SuccessAuthenticationDto result = registrationService.processRegister(registerDto);

        verify(userRepository).save(user);
        assertEquals(accessToken, result.getToken());
        assertEquals(refreshToken, result.getRefreshToken());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterDto registerDto = new RegisterDto(
                "username",
                "Full Name",
                "Password123",
                "email@example.com",
                "1234567890",
                "Group-1",
                "Some University"
        );

        when(userRepository.existsByUniqueFields(
                registerDto.getUsername(),
                registerDto.getEmail(),
                registerDto.getPhoneNumber()
        )).thenReturn(true);

        assertThrows(UserExistsException.class, () -> registrationService.processRegister(registerDto));
    }
}
