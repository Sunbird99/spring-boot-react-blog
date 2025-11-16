package com.groupname.blog.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Value("${jwt.secret}")
    private String secretKey;// Mock secret key (32 chars for HMAC-SHA)

    private final Long jwtExpiryMs = 86400000L; // 24 hours expiration

    private String email;
    private String password;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock @Value fields

        // Initialize test data
        email = "test@example.com";
        password = "password123";

        userDetails = User.withUsername(email)
                .password(password)
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Authenticate user with valid credentials")
    void testAuthenticate() {
        // Mock the AuthenticationManager
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(authentication);

        // Mock UserDetailsService
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Call method
        UserDetails authenticatedUser = authenticationService.authenticate(email, password);

        // Verify and assert
        assertThat(authenticatedUser.getUsername()).isEqualTo(email);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(email);
    }

    @Test
    @DisplayName("Authenticate user with invalid credentials should throw exception")
    void testAuthenticateWithInvalidCredentials() {
        // Mock an authentication failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Call method and expect exception
        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(email, password));

        // Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(email);
    }

    @Test
    @DisplayName("Generate JWT for authenticated user")
    void testGenerateToken() {
        // Call method
        String token = authenticationService.generateToken(userDetails);

        // Verify output
        assertThat(token).isNotNull();
        assertThat(token).contains(".");
    }

    @Test
    @DisplayName("Validate valid JWT token")
    void testValidateToken() {
        // Generate a valid token
        String token = authenticationService.generateToken(userDetails);

        // Call validateToken method
        UserDetails validatedUser = authenticationService.validateToken(token);

        // Verify user details
        assertThat(validatedUser.getUsername()).isEqualTo(email);
    }

    @Test
    @DisplayName("Validate invalid JWT token should throw exception")
    void testValidateInvalidToken() {
        // Invalid token
        String invalidToken = "invalid.token.value";

        // Call validateToken and expect exception
        assertThrows(RuntimeException.class, () -> authenticationService.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Extract username from valid JWT token")
    void testExtractUsername() {
        // Generate a valid token
        String token = authenticationService.generateToken(userDetails);

        // Call method by reflection (private method)
        String extractedUsername = authenticationService.validateToken(token)
                .getUsername();

        // Verify extracted username matches the userDetails
        assertThat(extractedUsername).isEqualTo(email);
    }
}