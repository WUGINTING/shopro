package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

/**
 * 忘記密碼：寄送重設連結（1 小時內有效），以連結設定新密碼。
 * - 不透露 Email 是否已註冊：無論帳號是否存在都回覆相同訊息
 * - 連結綁定目前密碼的指紋，密碼一旦變更（包含用此連結重設後）舊連結即失效
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    static final String PURPOSE = "password-reset";
    private static final long TOKEN_TTL_MILLIS = 60L * 60 * 1000;
    public static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.admin-store-url:}")
    private String adminStoreUrl;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    public void requestReset(String email) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException("目前無法寄送重設密碼信，請聯繫客服協助");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException("請輸入 Email");
        }
        userRepository.findByEmail(email.trim())
                .filter(user -> !Boolean.FALSE.equals(user.getEnabled()))
                .ifPresent(user -> send(mailSender, user));
    }

    private void send(JavaMailSender mailSender, User user) {
        String token = createToken(user);
        String link = adminStoreUrl.replaceAll("/+$", "") + "/reset-password?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            if (mailFrom != null && !mailFrom.isBlank()) {
                helper.setFrom(mailFrom, storeName);
            }
            helper.setTo(user.getEmail());
            helper.setSubject("【" + storeName + "】重設密碼");
            helper.setText(user.getUsername() + " 您好：\n\n我們收到重設密碼的申請，請於 1 小時內點擊以下連結設定新密碼：\n"
                    + link + "\n\n您的帳號：" + user.getUsername()
                    + "\n\n若您沒有申請重設密碼，請忽略此信，您的密碼不會變更。\n\n" + storeName + " 敬上\n", false);
            mailSender.send(message);
        } catch (Exception e) {
            // 不讓對方從錯誤判斷帳號是否存在，只記錄
            log.error("Failed to send password reset email to user {}", user.getId(), e);
        }
    }

    String createToken(User user) {
        return jwtService.generatePurposeToken(PURPOSE, String.valueOf(user.getId()),
                Map.of("pwd", fingerprint(user.getPassword() + "|" + normalize(user.getEmail()))), TOKEN_TTL_MILLIS);
    }

    @Transactional
    public User reset(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH || newPassword.length() > 100) {
            throw new BusinessException("新密碼長度需為 " + MIN_PASSWORD_LENGTH + " 到 100 個字元");
        }
        Claims claims = jwtService.parsePurposeToken(token, PURPOSE)
                .orElseThrow(() -> new BusinessException("重設連結無效或已過期，請重新申請"));
        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new BusinessException("重設連結無效或已過期，請重新申請"));
        // 指紋包含密碼與 Email：密碼已變更（連結用過）或帳號 Email 已變更時連結失效，
        // 避免先申請連結、再把 Email 改成別人的，藉此把他人 Email 標記為已驗證
        if (!fingerprint(user.getPassword() + "|" + normalize(user.getEmail())).equals(claims.get("pwd"))) {
            throw new BusinessException("此重設連結已使用過或已失效，請重新申請");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        // 能收到信代表擁有此信箱，順便完成 Email 驗證
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String fingerprint(String passwordHash) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(String.valueOf(passwordHash).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
