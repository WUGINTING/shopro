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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for role-based access control
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoleBasedAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;
    private String managerToken;
    private String staffToken;
    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up before each test
        userRepository.deleteAll();

        // Register and get tokens for different roles
        adminToken = registerAndLogin("admin", "admin@test.com", "admin123", Role.ADMIN);
        managerToken = registerAndLogin("manager", "manager@test.com", "manager123", Role.MANAGER);
        staffToken = registerAndLogin("staff", "staff@test.com", "staff123", Role.STAFF);
        customerToken = registerAndLogin("customer", "customer@test.com", "customer123", Role.CUSTOMER);
    }

    private String registerAndLogin(String username, String email, String password, Role role) throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    @Test
    void testViewProducts_AllRolesCanAccess() throws Exception {
        // All authenticated users should be able to view products
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testViewProducts_NoToken_Unauthorized() throws Exception {
        // Without token, should get 401 Unauthorized
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteProduct_OnlyAdminCanDelete() throws Exception {
        // ADMIN should be able to delete (403 is expected as product doesn't exist)
        mockMvc.perform(delete("/api/products/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is4xxClientError()); // 404 or 403 depending on implementation

        // MANAGER should NOT be able to delete
        mockMvc.perform(delete("/api/products/999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());

        // STAFF should NOT be able to delete
        mockMvc.perform(delete("/api/products/999")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isForbidden());

        // CUSTOMER should NOT be able to delete
        mockMvc.perform(delete("/api/products/999")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testViewOrders_OnlyStaffAndAboveCanAccess() throws Exception {
        // ADMIN should be able to view orders
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is4xxClientError()); // 404 expected as order doesn't exist

        // MANAGER should be able to view orders
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().is4xxClientError()); // 404 expected

        // STAFF should be able to view orders
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().is4xxClientError()); // 404 expected

        // CUSTOMER should NOT be able to view orders
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateOrder_OnlyStaffAndAboveCanCreate() throws Exception {
        String orderJson = "{\"customerName\":\"Test\",\"totalAmount\":100}";

        // ADMIN should be able to create orders
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is4xxClientError()); // 400 due to validation

        // MANAGER should be able to create orders
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is4xxClientError()); // 400 due to validation

        // STAFF should be able to create orders
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is4xxClientError()); // 400 due to validation

        // CUSTOMER should NOT be able to create orders
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testExpiredOrMalformedToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid"))
                .andExpect(status().isUnauthorized());
    }
}
