package com.techstud.sch_auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.exception.InvalidJwtTokenException;
import com.techstud.sch_auth.security.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
        ReflectionTestUtils.setField(tokenService, "SECRET_KEY", "mockSecretKey");
        ReflectionTestUtils.setField(tokenService, "PARSER_SECRET_KEY", "mockParserSecretKey");

        tokenService.initAlgorithms();
    }

    @Test
    public void testInitAlgorithms() {
        assertNotNull(ReflectionTestUtils.getField(tokenService, "authAlgorithm"));
        assertNotNull(ReflectionTestUtils.getField(tokenService, "parserAlgorithm"));
    }

    @Test
    public void testFailureInitAlgorithms() {
        ReflectionTestUtils.setField(tokenService, "SECRET_KEY", "");
        assertThrows(IllegalArgumentException.class, () -> tokenService.initAlgorithms());
    }

    @Test
    public void testGenerateToken_Success() {
        User user = new User();
        user.setUsername("mockUsername");
        user.setRoles(Set.of(new Role("USER"), new Role("ADMIN")));

        String token = tokenService.generateToken(user, 10);
        DecodedJWT jwt = JWT.decode(token);

        assertEquals("sch-auth", jwt.getIssuer());
        assertEquals(List.of("sch-main"), new ArrayList<>(jwt.getAudience()));
        assertEquals("mockUsername", jwt.getSubject());
        assertEquals(Set.of("USER", "ADMIN"), Set.of("USER", "ADMIN"));
        assertNotNull(jwt.getExpiresAt());
    }

    @Test
    public void testGenerateRefreshToken_Success() {
        User user = new User();
        user.setUsername("mockUsername");

        String token = tokenService.generateRefreshToken(user, 1);
        DecodedJWT jwt = JWT.decode(token);

        assertEquals("sch-auth", jwt.getIssuer());
        assertEquals("refresh", jwt.getClaim("type").asString());
        assertEquals("mockUsername", jwt.getSubject());
        assertNotNull(jwt.getExpiresAt());
    }

    @Test
    public void testVerifyToken_Success() {
        User user = new User();
        user.setUsername("mockUsername");

        String token = "Bearer " + tokenService.generateToken(user, 5);
        DecodedJWT jwt = tokenService.verifyToken(token, tokenService.getAlgorithmForIssuer("sch-auth"));

        assertEquals("sch-auth", jwt.getIssuer());
        assertEquals("mockUsername", jwt.getSubject());
    }

    @Test
    public void testVerifyToken_Failure() {
        String invalidToken = "Bearer invalidToken";

        assertThrows(InvalidJwtTokenException.class, () -> tokenService.verifyToken(invalidToken,
                tokenService.getAlgorithmForIssuer("sch-auth")));
    }

    @Test
    public void testGetAlgorithmForIssuer_Success() {
        Algorithm authAlgorithm = tokenService.getAlgorithmForIssuer("sch-auth");
        Algorithm parserAlgorithm = tokenService.getAlgorithmForIssuer("sch-parser");

        assertNotNull(parserAlgorithm);
        assertNotNull(authAlgorithm);
        assertThrows(IllegalArgumentException.class, () -> tokenService.getAlgorithmForIssuer("unknown"));
    }

    @Test
    public void testDecodeIssuer_Success() {
        User user = new User();
        user.setUsername("mockUsername");

        String token = tokenService.generateToken(user, 5);
        String issuer = JWT.decode(token).getIssuer();

        assertEquals("sch-auth", issuer);
    }

    @Test
    public void testDecodeIssuer_Failure() {
        String invalidToken = "Bearer invalidToken";

        assertThrows(IllegalArgumentException.class, () ->
                tokenService.decodeIssuer(invalidToken));
    }
}
