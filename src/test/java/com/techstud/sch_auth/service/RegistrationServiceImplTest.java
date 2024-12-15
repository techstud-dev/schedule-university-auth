package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.UserExistsException;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@Nested
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtGenerateService jwtGenerateService;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processRegister_ShouldCreateNewUser_WhenUserIsUnique() throws UserExistsException {
        RegisterDto registerDto = new RegisterDto("username", "password", "email@test.com", "1234567890");

        when(userRepository.existsByUniqueFields("username", "email@test.com", "1234567890"))
                .thenReturn(false);

        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(userRole));

        User newUser = new User();
        newUser.setUsername("username");
        newUser.setEmail("email@test.com");
        newUser.setPhoneNumber("1234567890");
        newUser.setRole(userRole);

        when(userFactory.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(newUser);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        when(jwtGenerateService.generateToken(newUser, 1)).thenReturn(accessToken);
        when(jwtGenerateService.generateRefreshToken(newUser, 2)).thenReturn(refreshToken);

        SuccessAuthenticationDto response = registrationService.processRegister(registerDto);

        assertNotNull(response);
        assertEquals(accessToken, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(userRepository).save(newUser);
    }

    @Test
    void processRegister_ShouldCreateNewRole_WhenRoleDoesNotExist() throws UserExistsException {
        RegisterDto registerDto = new RegisterDto("username", "password", "email@test.com", "1234567890");

        when(userRepository.existsByUniqueFields("username", "email@test.com", "1234567890"))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.empty());

        Role newRole = new Role();
        newRole.setName("USER");
        when(roleRepository.save(any(Role.class)))
                .thenReturn(newRole);

        User newUser = new User();
        newUser.setUsername("username");
        newUser.setEmail("email@test.com");
        newUser.setPhoneNumber("1234567890");
        newUser.setRole(newRole);

        when(userFactory.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(newUser);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        when(jwtGenerateService.generateToken(newUser, 1)).thenReturn(accessToken);
        when(jwtGenerateService.generateRefreshToken(newUser, 2)).thenReturn(refreshToken);

        SuccessAuthenticationDto response = registrationService.processRegister(registerDto);

        assertNotNull(response);
        assertEquals(accessToken, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(newUser);
    }

    @Test
    void processRegister_ShouldThrowException_WhenUserAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("username", "password", "email@test.com", "1234567890");

        when(userRepository.existsByUniqueFields("username", "email@test.com", "1234567890"))
                .thenReturn(true);

        assertThrows(UserExistsException.class, () -> registrationService.processRegister(registerDto));

        verify(userRepository, never()).save(any(User.class));
    }
}
