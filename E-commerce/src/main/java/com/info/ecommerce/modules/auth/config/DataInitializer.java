package com.info.ecommerce.modules.auth.config;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 首次啟動（使用者資料表為空）時建立初始管理員帳號。
 * <ul>
 *   <li>管理員密碼取自 app.bootstrap.admin-password（環境變數 ADMIN_INITIAL_PASSWORD）；
 *       未設定時產生隨機密碼並只在啟動日誌中顯示一次。</li>
 *   <li>示範帳號（manager / staff / customer）僅在 app.bootstrap.demo-users=true 時建立，正式環境請勿開啟。</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin-email:admin@example.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    @Value("${app.bootstrap.demo-users:false}")
    private boolean createDemoUsers;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping data initialization");
            return;
        }

        boolean generated = adminPassword == null || adminPassword.isBlank();
        String password = generated ? randomPassword() : adminPassword;
        createUser(adminUsername, adminEmail, password, Role.ADMIN);
        if (generated) {
            log.warn("Created initial ADMIN user '{}' with generated password: {}  "
                    + "(set ADMIN_INITIAL_PASSWORD to choose one, and change it after first login)", adminUsername, password);
        } else {
            log.info("Created initial ADMIN user '{}' (password from ADMIN_INITIAL_PASSWORD)", adminUsername);
        }

        if (createDemoUsers) {
            createUser("manager", "manager@example.com", "manager123", Role.MANAGER);
            createUser("staff", "staff@example.com", "staff123", Role.STAFF);
            createUser("customer", "customer@example.com", "customer123", Role.CUSTOMER);
            log.warn("Created DEMO users manager/manager123, staff/staff123, customer/customer123 "
                    + "(app.bootstrap.demo-users=true; never enable this in production)");
        }
    }

    private void createUser(String username, String email, String rawPassword, Role role) {
        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .enabled(true)
                .build());
    }

    private static String randomPassword() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
