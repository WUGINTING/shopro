package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PasswordResetService passwordResetService;

    @Test
    void resetLink_setsNewPassword_andCanOnlyBeUsedOnce() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User user = userRepository.save(User.builder()
                .username("forgetful-" + suffix).email("forgetful-" + suffix + "@example.com")
                .password(passwordEncoder.encode("OldPassw0rd")).role(Role.CUSTOMER).enabled(true).emailVerified(false)
                .build());
        String token = passwordResetService.createToken(user);

        // 密碼太短
        mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"short\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"NewPassw0rd!\"}"))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("NewPassw0rd!", updated.getPassword()));
        assertTrue(updated.isEmailConfirmed(), "能收到重設信代表擁有信箱");

        // 同一連結不可再次使用
        mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"Another0ne!\"}"))
                .andExpect(status().isBadRequest());

        // 新密碼可登入
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + user.getUsername() + "\",\"password\":\"NewPassw0rd!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void resetToken_isNotALoginToken_andBadTokenRejected() throws Exception {
        mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"not-a-token\",\"newPassword\":\"NewPassw0rd!\"}"))
                .andExpect(status().isBadRequest());
        // 測試環境未設定寄信：明確告知無法寄送（不透露帳號是否存在）
        mockMvc.perform(post("/api/auth/password-reset").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetLink_isInvalidAfterEmailChange_soItCannotVerifySomeoneElsesEmail() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User attacker = userRepository.save(User.builder()
                .username("attacker-" + suffix).email("attacker-" + suffix + "@example.com")
                .password(passwordEncoder.encode("OldPassw0rd")).role(Role.CUSTOMER).enabled(true).emailVerified(true)
                .build());
        String token = passwordResetService.createToken(attacker);
        attacker.setEmail("victim-" + suffix + "@example.com");
        attacker.setEmailVerified(false);
        userRepository.save(attacker);

        mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"NewPassw0rd!\"}"))
                .andExpect(status().isBadRequest());
        org.junit.jupiter.api.Assertions.assertFalse(userRepository.findById(attacker.getId()).orElseThrow().isEmailConfirmed());
    }
}
