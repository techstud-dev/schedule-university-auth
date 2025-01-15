package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
import com.techstud.sch_auth.entity.RefreshToken;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.BadCredentialsException;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.RoleRepository;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.security.JwtGenerateService;
import com.techstud.sch_auth.service.AbstractAuthService;
import com.techstud.sch_auth.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class LoginServiceImpl extends AbstractAuthService implements LoginService {

    private final BCryptPasswordEncoder passwordEncoder;

    public LoginServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            JwtGenerateService jwtGenerateService,
                            BCryptPasswordEncoder passwordEncoder) {
        super(userRepository, roleRepository, jwtGenerateService);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SuccessAuthenticationDto processLogin(LoginDto loginDto) {
        if (loginDto.getIdentificationField() == null || loginDto.getIdentificationField().trim().isEmpty()) {
            throw new BadCredentialsException();
        }

        User user = findUserByIdentificationFields(loginDto);

        if (!passwordValidation(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException();
        }
        String accessToken = jwtGenerateService.generateToken(user, 1);
        String refreshTokenString = jwtGenerateService.generateRefreshToken(user, 2);

        embedRefreshToken(user, refreshTokenString);
        userRepository.save(user);

        log.info("Request id: {} already logged in", loginDto.getRequestId());
        return buildSuccessResponse(accessToken, user.getRefreshToken());
    }

    private User findUserByIdentificationFields(LoginDto loginDto) {
        String identificationField = loginDto.getIdentificationField();

        return userRepository.findByUsernameIgnoreCase(identificationField)
                .or(() -> userRepository.findByEmailIgnoreCase(identificationField))
                .or(() -> userRepository.findByPhoneNumber(identificationField))
                .orElseThrow(UserNotFoundException::new);
    }

    private boolean passwordValidation(String password, String hashedPassword) {
        if (password == null) {
            throw new BadCredentialsException();
        }
        return passwordEncoder.matches(password, hashedPassword);
    }

    private void embedRefreshToken(User user, String refreshTokenString) {
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS);
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
