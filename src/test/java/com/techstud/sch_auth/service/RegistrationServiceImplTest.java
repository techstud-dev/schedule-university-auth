package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.RegisterDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.UserExistsException;
import com.techstud.sch_auth.repository.RoleRepository;
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
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterDto registerDto = new RegisterDto("testUser", "password123", "test@example.com", "+123456789");
        Role userRole = new Role("USER");
        User newUser = User.builder()
                .username("testUser")
                .password("encryptedPassword")
                .email("test@example.com")
                .phoneNumber("+123456789")
                .roles(Set.of(userRole))
                .build();

        when(userRepository.existsByUniqueFields(registerDto.getUsername(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userFactory.createUser(registerDto.getUsername(), registerDto.getPassword(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(newUser);
        when(tokenService.generateToken(newUser, 15)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(newUser, 1)).thenReturn("refreshToken");

        SuccessAuthenticationDto result = registrationService.processRegister(registerDto);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(userRepository).save(newUser);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserExists() {
        RegisterDto registerDto = new RegisterDto("testUser", "password123", "test@example.com", "+123456789");
        when(userRepository.existsByUniqueFields(registerDto.getUsername(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(true);

        assertThrows(UserExistsException.class, () -> registrationService.processRegister(registerDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateRoleWhenNotFound() {
        RegisterDto registerDto = new RegisterDto("testUser", "password123", "test@example.com", "+123456789");
        Role userRole = new Role("USER");
        User newUser = User.builder()
                .username("testUser")
                .password("encryptedPassword")
                .email("test@example.com")
                .phoneNumber("+123456789")
                .roles(Set.of(userRole))
                .build();

        when(userRepository.existsByUniqueFields(registerDto.getUsername(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenReturn(userRole);
        when(userFactory.createUser(registerDto.getUsername(), registerDto.getPassword(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(newUser);
        when(tokenService.generateToken(newUser, 15)).thenReturn("accessToken");
        when(tokenService.generateRefreshToken(newUser, 1)).thenReturn("refreshToken");

        SuccessAuthenticationDto result = registrationService.processRegister(registerDto);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void shouldHandleTokenGenerationFailure() {
        RegisterDto registerDto = new RegisterDto("testUser", "password123", "test@example.com", "+123456789");
        Role userRole = new Role("USER");
        User newUser = User.builder()
                .username("testUser")
                .password("encryptedPassword")
                .email("test@example.com")
                .phoneNumber("+123456789")
                .roles(Set.of(userRole))
                .build();

        when(userRepository.existsByUniqueFields(registerDto.getUsername(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userFactory.createUser(registerDto.getUsername(), registerDto.getPassword(), registerDto.getEmail(), registerDto.getPhoneNumber()))
                .thenReturn(newUser);
        when(tokenService.generateToken(newUser, 15)).thenThrow(new RuntimeException("Token generation failed"));

        assertThrows(RuntimeException.class, () -> registrationService.processRegister(registerDto));
        verify(userRepository, never()).save(any());
    }
}
