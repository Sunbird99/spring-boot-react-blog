package com.groupname.blog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupname.blog.domain.dtos.LoginRequest;
import com.groupname.blog.domain.dtos.AuthResponse;
import com.groupname.blog.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize MockMvc with AuthController
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Login with valid credentials returns JWT token")
    void testLoginWithValidCredentials() throws Exception {
        // Mock request and response
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("mock-jwt-token");

        // Mock AuthenticationService response
        when(authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn((UserDetails) authResponse);

        // Perform POST request
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        // Verify response content
    }

    @Test
    @DisplayName("Login with invalid credentials returns 401 Unauthorized")
    void testLoginWithInvalidCredentials() throws Exception {
        // Mock request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@example.com");
        loginRequest.setPassword("wrongpassword");

        // Mock AuthenticationService exception for unauthorized access
        when(authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Perform POST request
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
