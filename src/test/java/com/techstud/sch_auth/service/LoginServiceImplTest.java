package com.techstud.sch_auth.service;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.BadCredentialsException;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtGenerateService jwtGenerateService;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    public void testLogin_Success() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        LoginServiceImpl loginService = new LoginServiceImpl(userRepository, roleRepository, jwtGenerateService, passwordEncoder);

        LoginDto request = new LoginDto();
        request.setIdentificationField("kabarx@gmail.com");
        request.setPassword("passwordSome");

        User user = new User();
        user.setEmail("kabarx@gmail.com");
        user.setPassword(passwordEncoder.encode("passwordSome"));

        when(userRepository.existsByUniqueFields("kabarx@gmail.com", "kabarx@gmail.com", "kabarx@gmail.com")).thenReturn(true);
        when(userRepository.findByEmailIgnoreCase("kabarx@gmail.com")).thenReturn(Optional.of(user));
        when(jwtGenerateService.generateToken(user)).thenReturn("accessToken");
        when(jwtGenerateService.generateRefreshToken(user)).thenReturn("refreshToken");

        SuccessAuthenticationDto response = loginService.processLogin(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentificationField("user@example.com");

        when(userRepository.existsByUniqueFields("user@example.com", "user@example.com", "user@example.com")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> loginService.processLogin(loginDto));
    }

    @Test
    public void testLogin_InvalidPassword() {
        LoginDto loginDto = new LoginDto();
        loginDto.setIdentificationField("user@example.com");
        loginDto.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashedPassword");

        when(userRepository.existsByUniqueFields("user@example.com", "user@example.com", "user@example.com")).thenReturn(true);
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

        lenient().when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            loginService.processLogin(loginDto);
        });
    }
}
