package com.demo.oms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private AuthController.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new AuthController.LoginRequest();
    }

    @Test
    void login_Success() {
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");
        when(jwtUtil.generateToken("admin")).thenReturn("test.jwt.token");
        
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test.jwt.token", response.getBody().get("token"));
        assertEquals("Bearer", response.getBody().get("type"));
        verify(jwtUtil).generateToken("admin");
    }

    @Test
    void login_InvalidCredentials() {
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrongpassword");
        
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);
        
        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", response.getBody().get("error"));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void login_InvalidUsername() {
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("password");
        
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);
        
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void loginRequest_GettersAndSetters() {
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");
        
        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("testpass", loginRequest.getPassword());
    }
}
