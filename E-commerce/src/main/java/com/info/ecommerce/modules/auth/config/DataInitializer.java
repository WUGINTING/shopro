package com.info.ecommerce.modules.auth.config;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initialization component to seed sample users with different roles
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if users already exist
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping data initialization");
            return;
        }

        log.info("Initializing sample users with different roles...");

        // Create ADMIN user
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("Created ADMIN user: username=admin, password=admin123");

        // Create MANAGER user
        User manager = User.builder()
                .username("manager")
                .email("manager@example.com")
                .password(passwordEncoder.encode("manager123"))
                .role(Role.MANAGER)
                .enabled(true)
                .build();
        userRepository.save(manager);
        log.info("Created MANAGER user: username=manager, password=manager123");

        // Create STAFF user
        User staff = User.builder()
                .username("staff")
                .email("staff@example.com")
                .password(passwordEncoder.encode("staff123"))
                .role(Role.STAFF)
                .enabled(true)
                .build();
        userRepository.save(staff);
        log.info("Created STAFF user: username=staff, password=staff123");

        // Create CUSTOMER user
        User customer = User.builder()
                .username("customer")
                .email("customer@example.com")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userRepository.save(customer);
        log.info("Created CUSTOMER user: username=customer, password=customer123");

        log.info("Sample users initialization completed successfully!");
    }
}
