package com.info.ecommerce.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for role-based access control (SecurityConfig + method security + ownership checks)
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        // 員工帳號只能由後台建立，這裡直接寫入資料庫
        User admin = userRepository.save(User.builder()
                .username("rbac-admin")
                .email("rbac-admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build());
        adminToken = jwtService.generateToken(admin);

        customerToken = register("rbac-customer", "customer@test.com", "customer123", Role.CUSTOMER);
    }

    private String register(String username, String email, String password, Role requestedRole) throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(requestedRole)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("token").asText();
    }

    private Order saveOrder(String orderNumber, String email) {
        return orderRepository.save(Order.builder()
                .orderNumber(orderNumber)
                .customerId(999L)
                .customerName("顧客")
                .customerEmail(email)
                .status(OrderStatus.PENDING_PAYMENT)
                .pickupType(PickupType.DELIVERY)
                .subtotalAmount(new BigDecimal("100"))
                .totalAmount(new BigDecimal("100"))
                .isDraft(false)
                .build());
    }

    @Test
    void testViewProducts_PublicWithoutToken() throws Exception {
        // 前台商城需要匿名瀏覽商品
        mockMvc.perform(get("/api/products")).andExpect(status().isOk());
        mockMvc.perform(get("/api/products").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testRegister_CannotChooseAdminRole() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegisterRequest.builder()
                                .username("attacker")
                                .email("attacker@test.com")
                                .password("password123")
                                .role(Role.ADMIN)
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("token").asText();
        mockMvc.perform(get("/api/crm/members").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminApi_NoToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/crm/members")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/system/payment-config")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/orders/batch/status").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminApi_CustomerForbidden_AdminAllowed() throws Exception {
        mockMvc.perform(get("/api/crm/members").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/crm/members").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProduct_OnlyAdminCanDelete() throws Exception {
        mockMvc.perform(delete("/api/products/999").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCustomerCannotCreateOrderWithOwnPrices() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testStorefrontCheckout_IsPublic() throws Exception {
        // 未登入可呼叫；空購物車回 400 驗證錯誤而不是 401
        mockMvc.perform(post("/api/storefront/orders/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }

    private void markCustomerEmailVerified() {
        User customer = userRepository.findByEmail("customer@test.com").orElseThrow();
        customer.setEmailVerified(true);
        userRepository.save(customer);
    }

    @Test
    void testUnverifiedEmail_CannotReadOrdersPlacedWithThatEmail() throws Exception {
        // 任何人都能註冊他人的 Email；未驗證前不可存取該 Email 的訂單
        Order victims = saveOrder("ORDVICTIM0000000001", "customer@test.com");
        mockMvc.perform(get("/api/orders/" + victims.getId()).header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testEmailVerificationFlow_AndPurposeTokenIsNotALoginToken() throws Exception {
        User customer = userRepository.findByEmail("customer@test.com").orElseThrow();
        String verifyToken = jwtService.generatePurposeToken("email-verify", String.valueOf(customer.getId()),
                java.util.Map.of("email", "customer@test.com"), 60_000);

        // 驗證連結的 token 不能當作登入憑證
        mockMvc.perform(get("/api/auth/profile").header("Authorization", "Bearer " + verifyToken))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/email-verification/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + verifyToken + "\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/auth/profile").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emailVerified").value(true));

        // 偽造 / 過期的連結無效
        mockMvc.perform(post("/api/auth/email-verification/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"bogus\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCustomerCanOnlyReadOwnOrders() throws Exception {
        markCustomerEmailVerified();
        Order own = saveOrder("ORDOWN0000000000001", "customer@test.com");
        Order other = saveOrder("ORDOTHER00000000001", "someone@else.com");

        mockMvc.perform(get("/api/orders/" + own.getId()).header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value("ORDOWN0000000000001"));
        mockMvc.perform(get("/api/orders/" + other.getId()).header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/orders/" + other.getId()).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCustomerMyOrders_WithQueryString() throws Exception {
        markCustomerEmailVerified();
        // RegexRequestMatcher 會比對查詢參數；帶 ?page= 的請求也必須允許
        mockMvc.perform(get("/api/orders/my?page=0&size=20").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testPaymentCreate_RequiresLoginAndOwnership() throws Exception {
        Order other = saveOrder("ORDOTHER00000000002", "someone@else.com");
        String body = "{\"orderNumber\":\"" + other.getOrderNumber() + "\",\"amount\":1,\"productName\":\"x\"}";

        mockMvc.perform(post("/api/payment-gateway/create").param("gateway", "ECPAY")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/payment-gateway/create").param("gateway", "ECPAY")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void batchOrderDeleteAndExport_requireManagerOrAdmin() throws Exception {
        User staff = userRepository.save(User.builder()
                .username("rbac-staff").email("rbac-staff@test.com")
                .password(passwordEncoder.encode("staff123")).role(Role.STAFF).enabled(true).build());
        String staffToken = jwtService.generateToken(staff);

        mockMvc.perform(delete("/api/orders/batch").header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content("[999999]"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/orders/batch/export").header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/orders/batch/status").header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"orderIds\":[999999],\"status\":\"CANCELLED\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/orders/batch").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("[999999]"))
                .andExpect(status().isOk());
    }
}
