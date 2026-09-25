package com.info.ecommerce.modules.crm;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.crm.repository.PointRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountAndContactIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PointRecordRepository pointRecordRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private String suffix;
    private String adminToken;

    @BeforeEach
    void setUp() {
        suffix = UUID.randomUUID().toString().substring(0, 8);
        adminToken = jwtService.generateToken(userRepository.save(User.builder()
                .username("acc-admin-" + suffix).email("acc-admin-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.ADMIN).enabled(true).build()));
    }

    private String customerToken(boolean verified) {
        return jwtService.generateToken(userRepository.save(User.builder()
                .username("acc-" + suffix).email("acc-" + suffix + "@example.com")
                .password(passwordEncoder.encode("x")).role(Role.CUSTOMER).enabled(true).emailVerified(verified).build()));
    }

    @Test
    void memberCenter_requiresVerifiedEmail_andSavesDefaultAddress() throws Exception {
        String unverified = customerToken(false);
        mockMvc.perform(get("/api/account/member").header("Authorization", "Bearer " + unverified))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.emailVerified").value(false));
        mockMvc.perform(put("/api/account/member").header("Authorization", "Bearer " + unverified)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"address\":\"x\"}"))
                .andExpect(status().isBadRequest());

        User user = userRepository.findByUsername("acc-" + suffix).orElseThrow();
        user.setEmailVerified(true);
        userRepository.save(user);
        mockMvc.perform(put("/api/account/member").header("Authorization", "Bearer " + unverified)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"林小明\",\"phone\":\"0911222333\",\"address\":\"台南市東區 1 號\",\"marketingOptIn\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.address").value("台南市東區 1 號"))
                .andExpect(jsonPath("$.data.levelName").value("一般會員"));
        Member member = memberRepository.findByEmail("acc-" + suffix + "@example.com").orElseThrow();
        assertTrue(member.getMarketingOptIn());

        mockMvc.perform(put("/api/account/member").header("Authorization", "Bearer " + unverified)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"phone\":\"123\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/account/member")).andExpect(status().isUnauthorized());
    }

    @Test
    void contactForm_validates_notifiesStaff_andThrottles() throws Exception {
        String body = "{\"name\":\"訪客\",\"email\":\"guest@example.com\",\"message\":\"想詢問團購價格\",\"subject\":\"團購\"}";
        mockMvc.perform(post("/api/storefront/contact").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\",\"message\":\"hi\"}"))
                .andExpect(status().isBadRequest());
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/storefront/contact").with(request -> { request.setRemoteAddr("10.9.9.9"); return request; })
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post("/api/storefront/contact").with(request -> { request.setRemoteAddr("10.9.9.9"); return request; })
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("次數過多")));
        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CONTACT_MESSAGE")));
    }

    @Test
    void manualPointAdjustments_areRecordedInLedger() throws Exception {
        Member member = memberRepository.save(Member.builder().name("積點會員").email("points-" + suffix + "@example.com").build());
        mockMvc.perform(post("/api/crm/members/" + member.getId() + "/points/add").param("points", "50")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/crm/members/" + member.getId() + "/points/deduct").param("points", "20")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        var records = pointRecordRepository.findByMemberId(member.getId(), Pageable.unpaged()).getContent();
        assertEquals(2, records.size());
        assertTrue(records.stream().anyMatch(record -> record.getPoints() == -20 && record.getBalanceAfter() == 30));
    }

    @Test
    void orderCsvExport_isExcelFriendly_andManagerOnly() throws Exception {
        mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON).content("""
                {"customerName":"=HYPERLINK(\\"x\\")","customerPhone":"0912345678","customerEmail":"csv-%s@example.com",
                 "shippingAddress":"台北","shippingMethod":"STORE_PICKUP","paymentMethod":"COD",
                 "items":[]}
                """.formatted(suffix))).andExpect(status().isBadRequest());

        byte[] csv = mockMvc.perform(get("/api/orders/batch/export.csv")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andReturn().getResponse().getContentAsByteArray();
        String text = new String(csv, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("﻿訂單編號"), "需有 BOM 與中文標題");

        String staffToken = jwtService.generateToken(userRepository.save(User.builder()
                .username("acc-staff-" + suffix).email("acc-staff-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.STAFF).enabled(true).build()));
        mockMvc.perform(get("/api/orders/batch/export.csv").header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isForbidden());
    }
}
