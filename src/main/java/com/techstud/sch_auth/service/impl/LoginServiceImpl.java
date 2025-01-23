package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.BadCredentialsException;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.TokenService;
import com.techstud.sch_auth.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginServiceImpl(UserRepository userRepository, TokenService tokenService,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SuccessAuthenticationDto processLogin(LoginDto loginDto) {
        User user = findUserByIdentificationField(loginDto.getIdentificationField());
        validatePassword(loginDto.getPassword(), user.getPassword());

        String accessToken = tokenService.generateToken(user, 15); // 15 минут, см. TokenService
        String refreshToken = tokenService.generateRefreshToken(user, 1); // 1 час

        user.setRefreshToken(new RefreshToken(refreshToken, Instant.now().plus(1, ChronoUnit.HOURS)));
        userRepository.save(user);

        log.info("User {} logged in successfully", user.getUsername());
        return new SuccessAuthenticationDto(accessToken, refreshToken);
    }

    private User findUserByIdentificationField(String field) {
        return userRepository.findByUsernameIgnoreCase(field)
                .or(() -> userRepository.findByEmailIgnoreCase(field))
                .or(() -> userRepository.findByPhoneNumber(field))
                .orElseThrow(UserNotFoundException::new);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("Invalid password.");
        }
    }
}
