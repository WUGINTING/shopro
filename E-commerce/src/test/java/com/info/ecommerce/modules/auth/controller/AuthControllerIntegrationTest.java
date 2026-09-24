package com.info.ecommerce.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.auth.dto.LoginRequest;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Register a user first
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login with the user
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testGoogleLogin_ForgedUnsignedToken_Rejected() throws Exception {
        // 未經 Google 簽章的 token 不可登入（過去只解碼 payload，可冒用任何 Email）
        String payload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"email\":\"admin@example.com\",\"name\":\"x\"}".getBytes());
        String forged = "eyJhbGciOiJub25lIn0." + payload + ".forged";

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idToken\":\"" + forged + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.token").doesNotExist());
    }
}
