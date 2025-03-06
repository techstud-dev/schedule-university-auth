package com.techstud.sch_auth.service.impl;

import com.techstud.sch_auth.dto.LogoutRequest;
import com.techstud.sch_auth.exception.UserNotFoundException;
import com.techstud.sch_auth.repository.UserRepository;
import com.techstud.sch_auth.service.LogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final UserRepository repository;

    @Override
    @Transactional
    public void clearRefreshToken(LogoutRequest request) {
        log.info("Clear refresh token, id: {}", request.requestId());
        int updated = repository.clearRefreshToken(request.refreshToken());

        if (updated == 0) {
            throw new UserNotFoundException("No user found with refresh token") ;
        }
    }
}
