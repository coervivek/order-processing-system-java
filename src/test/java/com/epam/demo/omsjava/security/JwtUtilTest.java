package com.epam.demo.omsjava.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForJWTTokenGenerationAndValidation12345");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken("testuser");
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_Success() {
        String token = jwtUtil.generateToken("testuser");
        
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ValidToken() {
        String token = jwtUtil.generateToken("testuser");
        
        boolean isValid = jwtUtil.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken() {
        boolean isValid = jwtUtil.validateToken("invalid.token.here");
        
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String token = jwtUtil.generateToken("testuser");
        
        boolean isValid = jwtUtil.validateToken(token);
        
        assertFalse(isValid);
    }
}
