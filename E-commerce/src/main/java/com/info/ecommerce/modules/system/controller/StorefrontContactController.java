package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.service.AdminNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 顧客聯絡表單：留言會出現在後台通知，並在設定寄信時寄到商店信箱（MAIL_FROM，可直接回覆顧客）
 */
@Slf4j
@RestController
@RequestMapping("/api/storefront/contact")
@RequiredArgsConstructor
@Tag(name = "聯絡表單", description = "顧客留言")
public class StorefrontContactController {

    private static final int MAX_PER_WINDOW = 5;
    private static final long WINDOW_SECONDS = 600;

    private final AdminNotificationService adminNotificationService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final Map<String, Deque<Instant>> recentByClient = new ConcurrentHashMap<>();

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    @Data
    public static class ContactRequest {
        @NotBlank(message = "請輸入姓名")
        @Size(max = 50, message = "姓名不可超過 50 字")
        private String name;

        @NotBlank(message = "請輸入 Email")
        @Email(message = "Email 格式不正確")
        @Size(max = 100, message = "Email 不可超過 100 字")
        private String email;

        @Size(max = 20, message = "電話不可超過 20 字")
        private String phone;

        @Size(max = 50, message = "主旨不可超過 50 字")
        private String subject;

        @NotBlank(message = "請輸入留言內容")
        @Size(min = 5, max = 400, message = "留言內容需為 5 到 400 字")
        private String message;
    }

    @PostMapping
    @Operation(summary = "送出聯絡表單")
    public ApiResponse<Void> submit(@Valid @RequestBody ContactRequest request, HttpServletRequest http) {
        throttle(clientKey(http));

        String subject = request.getSubject() == null || request.getSubject().isBlank() ? "一般詢問" : request.getSubject().trim();
        String contact = request.getEmail().trim() + (request.getPhone() == null || request.getPhone().isBlank() ? "" : " / " + request.getPhone().trim());
        adminNotificationService.createNotification(AdminNotificationType.CONTACT_MESSAGE, null, null,
                "顧客留言（" + subject + "）：" + request.getName().trim(),
                contact + "\n" + request.getMessage().trim());

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender != null && mailFrom != null && !mailFrom.isBlank()) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                helper.setFrom(mailFrom, storeName);
                helper.setTo(mailFrom);
                helper.setReplyTo(request.getEmail().trim());
                helper.setSubject("【顧客留言】" + subject + " - " + request.getName().trim());
                helper.setText("姓名：" + request.getName().trim() + "\n聯絡方式：" + contact
                        + "\n主旨：" + subject + "\n\n" + request.getMessage().trim()
                        + "\n\n（直接回覆此信即可回覆顧客）\n", false);
                mailSender.send(message);
            } catch (Exception e) {
                log.warn("Failed to forward contact message by email: {}", e.getMessage());
            }
        }
        return ApiResponse.success("已收到您的留言，我們會盡快與您聯繫", null);
    }

    private static String clientKey(HttpServletRequest http) {
        String forwarded = http.getHeader("X-Forwarded-For");
        return forwarded != null && !forwarded.isBlank() ? forwarded.split(",")[0].trim() : http.getRemoteAddr();
    }

    /** 同一來源 10 分鐘內最多 5 則，避免灌水 */
    private void throttle(String key) {
        Instant now = Instant.now();
        Deque<Instant> recent = recentByClient.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        while (!recent.isEmpty() && recent.peekFirst().isBefore(now.minusSeconds(WINDOW_SECONDS))) {
            recent.pollFirst();
        }
        if (recent.size() >= MAX_PER_WINDOW) {
            throw new BusinessException("留言次數過多，請稍後再試或直接來電");
        }
        recent.addLast(now);
        if (recentByClient.size() > 10_000) {
            recentByClient.clear();
        }
    }
}
