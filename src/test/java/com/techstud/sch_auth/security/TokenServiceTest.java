package com.techstud.sch_auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techstud.sch_auth.dto.ServiceDto;
import com.techstud.sch_auth.entity.Role;
import com.techstud.sch_auth.entity.User;
import com.techstud.sch_auth.security.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "SECRET_KEY", "mockSecretKey");
        ReflectionTestUtils.setField(tokenService, "PARSER_SECRET_KEY", "mockParserSecretKey");
        ReflectionTestUtils.setField(tokenService, "MAIN_SECRET_KEY", "mockMainSecretKey");
        ReflectionTestUtils.setField(tokenService, "authIssuer", "sch-auth");
        ReflectionTestUtils.setField(tokenService, "mainAudience", "sch-main");
        tokenService.initAlgorithms();
    }

    @Test
    public void testGenerateToken_User() {
        Role roleAdmin = new Role("ADMIN");
        Role roleUser = new Role("USER");

        User user = User.builder()
                .username("testUser")
                .password("password")
                .roles(Set.of(roleAdmin, roleUser))
                .build();

        String token = tokenService.generateToken(user, 15);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("sch-auth", decodedJWT.getIssuer());
        assertEquals("sch-main", decodedJWT.getAudience().get(0));
        assertEquals("testUser", decodedJWT.getSubject());
        assertEquals("access", decodedJWT.getClaim("type").asString());
        assertArrayEquals(
                new String[]{"ADMIN", "USER"},
                decodedJWT.getClaim("roles").asArray(String.class)
        );
    }

    @Test
    public void testGenerateRefreshToken_User() {
        Role roleUser = new Role("USER");

        User user = User.builder()
                .username("testUser")
                .password("password")
                .roles(Set.of(roleUser))
                .build();

        String token = tokenService.generateRefreshToken(user, 24);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("refresh", decodedJWT.getClaim("type").asString());
        assertEquals("sch-auth", decodedJWT.getIssuer());
        assertEquals("testUser", decodedJWT.getSubject());
    }

    @Test
    public void testVerifyToken_Success() {
        Role roleUser = new Role("USER");

        User user = User.builder()
                .username("testUser")
                .password("password")
                .roles(Set.of(roleUser))
                .build();

        String token = tokenService.generateToken(user, 15);

        DecodedJWT decodedJWT = tokenService.verifyToken("Bearer " + token, tokenService.getAlgorithmForIssuer("sch-auth"));
        assertNotNull(decodedJWT);
        assertEquals("testUser", decodedJWT.getSubject());
        assertEquals("USER", decodedJWT.getClaim("roles").asArray(String.class)[0]);
    }

    @Test
    public void testDecodeIssuer_Success() {
        Role roleUser = new Role("USER");

        User user = User.builder()
                .username("testUser")
                .password("password")
                .roles(Set.of(roleUser))
                .build();

        String token = tokenService.generateToken(user, 15);

        String issuer = tokenService.decodeIssuer("Bearer " + token);
        assertEquals("sch-auth", issuer);
    }

    @Test
    public void testDecodeIssuer_InvalidToken() {
        String invalidToken = "Bearer invalidToken";

        assertThrows(IllegalArgumentException.class, () -> tokenService.decodeIssuer(invalidToken));
    }

    @Test
    public void testGenerateToken_ServiceDto() {
        ServiceDto serviceInfo = new ServiceDto("testService");
        String token = tokenService.generateToken(serviceInfo);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("access", decodedJWT.getClaim("type").asString());
        assertEquals("testService", decodedJWT.getAudience().get(0));
    }

    @Test
    public void testGenerateRefreshToken_ServiceDto() {
        ServiceDto serviceInfo = new ServiceDto("testService");
        String token = tokenService.generateRefreshToken(serviceInfo);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("refresh", decodedJWT.getClaim("type").asString());
    }
}
