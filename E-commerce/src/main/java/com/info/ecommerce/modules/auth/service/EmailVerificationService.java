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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 會員 Email 驗證：寄送含簽章連結的驗證信，點擊後標記帳號 Email 已驗證。
 * 驗證連結 24 小時內有效，且綁定寄送當下的 Email（變更 Email 後舊連結失效）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    static final String PURPOSE = "email-verify";
    private static final long TOKEN_TTL_MILLIS = 24L * 60 * 60 * 1000;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.admin-store-url:}")
    private String adminStoreUrl;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    /**
     * 寄送驗證信給目前登入的使用者
     */
    public void sendVerification(User user) {
        if (user.isEmailConfirmed()) {
            throw new BusinessException("此 Email 已完成驗證");
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException("目前無法寄送驗證信，請聯繫客服協助驗證");
        }
        String token = createToken(user);
        String link = adminStoreUrl.replaceAll("/+$", "") + "/verify-email?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            if (mailFrom != null && !mailFrom.isBlank()) {
                helper.setFrom(mailFrom, storeName);
            }
            helper.setTo(user.getEmail());
            helper.setSubject("【" + storeName + "】請驗證您的 Email");
            helper.setText(user.getUsername() + " 您好：\n\n請點擊以下連結完成 Email 驗證（24 小時內有效）：\n"
                    + link + "\n\n完成驗證後即可在會員中心查看以此 Email 下的訂單。\n"
                    + "若您沒有申請此帳號，請忽略此信。\n\n" + storeName + " 敬上\n", false);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send verification email to user {}", user.getId(), e);
            throw new BusinessException("驗證信寄送失敗，請稍後再試");
        }
    }

    String createToken(User user) {
        return jwtService.generatePurposeToken(PURPOSE, String.valueOf(user.getId()),
                Map.of("email", user.getEmail()), TOKEN_TTL_MILLIS);
    }

    /**
     * 以驗證連結中的 token 完成驗證
     */
    @Transactional
    public User confirm(String token) {
        Claims claims = jwtService.parsePurposeToken(token, PURPOSE)
                .orElseThrow(() -> new BusinessException("驗證連結無效或已過期，請重新寄送驗證信"));
        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new BusinessException("驗證連結無效或已過期，請重新寄送驗證信"));
        Object email = claims.get("email");
        if (email == null || !email.toString().equalsIgnoreCase(user.getEmail())) {
            throw new BusinessException("Email 已變更，請重新寄送驗證信");
        }
        user.setEmailVerified(true);
        return userRepository.save(user);
    }
}
