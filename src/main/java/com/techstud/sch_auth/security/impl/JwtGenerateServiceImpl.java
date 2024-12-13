package com.techstud.sch_auth.security.impl;

import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.security.JwtGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class JwtGenerateServiceImpl implements JwtGenerateService {

    @Override
    public String generateToken(User user) {
        //TODO: Implement me
        return null;
    }

    @Override
    public String generateRefreshToken(User user) {
        //TODO: Implement me
        return null;
    }
}
