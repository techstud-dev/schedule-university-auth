package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.LoginDto;
import com.techstud.sch_auth.dto.SuccessAuthenticationDto;
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

import java.util.UUID;

@Slf4j
@Service("LOGIN_SERVICE")
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
            throw new BadCredentialsException("Identification field is missing or empty");
        }

        User user;
        try {
            user = findUserByIdentificationFields(loginDto);
        } catch (UserNotFoundException e) {
            log.error("Error during user search: {}", e.getMessage());
            throw e;
        }

        try {
            if (!passwordValidation(loginDto.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
        } catch (BadCredentialsException e) {
            log.error("Invalid password attempt for user: {}", user.getUsername());
            throw e;
        }

        String accessToken = jwtGenerateService.generateToken(user);
        String refreshToken = jwtGenerateService.generateRefreshToken(user);

        return buildSuccessResponse(accessToken, refreshToken);
    }

    private User findUserByIdentificationFields(LoginDto loginDto) {
        String identificationField = loginDto.getIdentificationField().trim();
        log.info("Searching for user with identification field: {}", identificationField);

        if (!userRepository.existsByUniqueFields(identificationField, identificationField, identificationField)) {
            throw new UserNotFoundException();
        }

        return userRepository.findByUsernameIgnoreCase(identificationField)
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(identificationField)
                        .orElseGet(() -> userRepository.findByPhoneNumber(identificationField)
                                .orElseThrow(UserNotFoundException::new)));
    }

    private boolean passwordValidation(String password, String hashedPassword) {
        try {
            if (!passwordEncoder.matches(password, hashedPassword)) {
                throw new BadCredentialsException("Invalid password");
            }
            return true;
        } catch (Exception e) {
            log.error("Error during password validation: {}", e.getMessage());
            throw new BadCredentialsException("Invalid password");
        }
    }

    private SuccessAuthenticationDto buildSuccessResponse(String accessToken, String refreshToken) {
        SuccessAuthenticationDto response = new SuccessAuthenticationDto();
        response.setRequestId(UUID.randomUUID().toString());
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
