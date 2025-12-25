package com.info.ecommerce.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Use existing admin user or create one if not exists
        adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User user = User.builder()
                            .username("admin")
                            .email("admin@example.com")
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .enabled(true)
                            .build();
                    return userRepository.save(user);
                });

        // Generate admin token
        adminToken = jwtService.generateToken(adminUser);
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/" + adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.email").value("admin@example.com"));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("adminupdated")
                .email("adminupdated@example.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        mockMvc.perform(put("/api/users/" + adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("adminupdated"))
                .andExpect(jsonPath("$.data.email").value("adminupdated@example.com"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Create a user to delete
        User user = User.builder()
                .username("todelete")
                .email("todelete@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testToggleUserStatus_Success() throws Exception {
        mockMvc.perform(patch("/api/users/" + adminUser.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false));
    }

    @Test
    void testCreateUser_WithoutAuth_ShouldFail() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isForbidden());
    }
}
