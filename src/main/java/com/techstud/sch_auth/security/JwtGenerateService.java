package com.techstud.sch_auth.security;

import com.techstud.sch_auth.entity.User;

public interface JwtGenerateService {

    String generateToken(User user);

    String generateRefreshToken(User user);

}
