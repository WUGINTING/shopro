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

    @Test
    void passwordChange_invalidatesOldTokens_andReturnsNewToken() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("pwduser").email("pwd@example.com").password("password123").role(Role.CUSTOMER).build();
        String oldToken = objectMapper.readTree(mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).path("data").path("token").asText();

        String body = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/profile")
                        .header("Authorization", "Bearer " + oldToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"password123\",\"newPassword\":\"newpassword456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String newToken = objectMapper.readTree(body).path("data").path("token").asText();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/profile")
                        .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/profile")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("pwduser"));
    }

    @Test
    void repeatedFailedLogins_lockTheAccountTemporarily() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("bruteuser").email("brute@example.com").password("password123").role(Role.CUSTOMER).build();
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login").with(request -> { request.setRemoteAddr("10.88.0.1"); return request; })
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"bruteuser\",\"password\":\"wrong-" + i + "\"}"))
                    .andExpect(status().is4xxClientError());
        }
        // 同一來源已暫停：即使密碼正確也要等待
        mockMvc.perform(post("/api/auth/login").with(request -> { request.setRemoteAddr("10.88.0.1"); return request; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bruteuser\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("次數過多")));
        // 單一來源無法藉此鎖住本人：從其他來源仍可登入
        mockMvc.perform(post("/api/auth/login").with(request -> { request.setRemoteAddr("10.88.0.2"); return request; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bruteuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void changingEmail_requiresTheCurrentPassword() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("mailuser").email("mail@example.com").password("password123").role(Role.CUSTOMER).build();
        String token = objectMapper.readTree(mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn().getResponse().getContentAsString()).path("data").path("token").asText();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/profile")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"mailuser\",\"email\":\"attacker@example.com\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/profile")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"mailuser\",\"email\":\"attacker@example.com\",\"currentPassword\":\"wrong\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/profile")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"mailuser\",\"email\":\"new@example.com\",\"currentPassword\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }
}
