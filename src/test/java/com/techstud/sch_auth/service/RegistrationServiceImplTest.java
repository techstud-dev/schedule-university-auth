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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {

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

    @Test
    void processRegister_shouldRegisterNewUser() throws UserExistsException {
        RegisterDto request = new RegisterDto();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhoneNumber("1234567890");
        request.setPassword("password");

        Role userRole = new Role();
        userRole.setName("USER");

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(request.getPassword());
        newUser.setRole(userRole);

        String accessToken = "mockedAccessToken";
        String refreshToken = "mockedRefreshToken";

        Mockito.when(userRepository.existsByUniqueFields(
                request.getUsername(),
                        request.getEmail(),
                        request.getPhoneNumber()))
                .thenReturn(false);
        Mockito.when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        Mockito.when(userFactory.createUser(
                        Mockito.eq(request.getUsername()),
                        Mockito.eq(request.getPassword()),
                        Mockito.eq(request.getEmail()),
                        Mockito.eq(request.getPhoneNumber())))
                .thenReturn(newUser);
        Mockito.when(jwtGenerateService.generateToken(newUser)).thenReturn(accessToken);
        Mockito.when(jwtGenerateService.generateRefreshToken(newUser)).thenReturn(refreshToken);

        SuccessAuthenticationDto result = registrationService.processRegister(request);

        Mockito.verify(userRepository).save(newUser);
        assertNotNull(result);
        assertEquals(accessToken, result.getToken());
        assertEquals(refreshToken, result.getRefreshToken());
    }

    @Test
    void processRegister_shouldThrowExceptionWhenUserExists() {
        RegisterDto request = new RegisterDto();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhoneNumber("1234567890");

        Mockito.when(userRepository.existsByUniqueFields(request.getUsername(), request.getEmail(), request.getPhoneNumber()))
                .thenReturn(true);

        assertThrows(UserExistsException.class, () -> registrationService.processRegister(request));
    }

    @Test
    void processRegister_shouldCreateNewRoleIfNotExists() {
        String roleName = "USER";

        Mockito.when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());
        Role role = new Role();
        role.setName(roleName);
        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(role);

        Role result = registrationService.getOrCreateUserRole();

        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
        assertNotNull(result);
        assertEquals(roleName, result.getName());
    }

    @Test
    void  getOrCreateUserRole_shouldReturnExistingRole() {
        String roleName = "USER";
        Role existingRole = new Role();
        existingRole.setName(roleName);

        Mockito.when(roleRepository.findByName(roleName)).thenReturn(Optional.of(existingRole));

        Role result = registrationService.getOrCreateUserRole();

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
        assertNotNull(result);
        assertEquals(roleName, result.getName());
    }
}
