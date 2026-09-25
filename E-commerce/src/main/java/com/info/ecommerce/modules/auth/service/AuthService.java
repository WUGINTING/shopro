package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.dto.AuthResponse;
import com.info.ecommerce.modules.auth.dto.LoginRequest;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.dto.UpdateProfileRequest;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("此帳號已被使用，請換一個帳號");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("此 Email 已註冊，請直接登入或使用忘記密碼");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // 公開註冊不可自選角色，避免任何人註冊成 ADMIN
                .role(Role.CUSTOMER)
                .enabled(true)
                // Email 需驗證後才會連結到該 Email 的訂單與會員資料
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        // 寄送 Email 驗證信（未設定寄信時略過，會員可稍後在會員中心重寄）
        emailVerificationService.sendVerificationQuietly(savedUser);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .emailVerified(savedUser.isEmailConfirmed())
                .build();
    }

    /** 帳號是否存在（登入限流只為實際存在的帳號建立帳號層級的計數） */
    @Transactional(readOnly = true)
    public boolean accountExists(String username) {
        return username != null && userRepository.existsByUsername(username);
    }

    public AuthResponse login(LoginRequest request) {
        // Get user from database first to check enabled status
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("使用者不存在或密碼錯誤"));

        // Check if user account is enabled
        if (!user.getEnabled()) {
            throw new BusinessException("此帳號已被停用，無法登入");
        }

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .emailVerified(user.isEmailConfirmed())
                .build();
    }

    /**
     * Get current user profile
     */
    public UserDTO getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("使用者不存在"));
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .emailVerified(user.isEmailConfirmed())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Update current user profile
     */
    @Transactional
    public UserDTO updateCurrentUserProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("使用者不存在"));

        boolean usernameChanged = false;
        boolean passwordChanged = false;
        // Update username if provided and different
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            usernameChanged = true;
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException("使用者名稱已存在：" + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email 已存在：" + request.getEmail());
            }
            user.setEmail(request.getEmail());
            // 變更 Email 後需重新驗證
            user.setEmailVerified(false);
        }

        // 變更密碼必須提供目前密碼
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()
                && (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank())) {
            throw new BusinessException("變更密碼請輸入目前密碼；忘記目前密碼可使用「忘記密碼」重設");
        }
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("目前密碼不正確");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            passwordChanged = true;
        }

        User updatedUser = userRepository.save(user);

        return UserDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .enabled(updatedUser.getEnabled())
                .emailVerified(updatedUser.isEmailConfirmed())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                // 帳號名稱或密碼變更後舊 token 失效，回傳新 token 讓目前裝置保持登入
                .token(usernameChanged || passwordChanged ? jwtService.generateToken(updatedUser) : null)
                .build();
    }

    /**
     * Google OAuth login/register
     * Validates Google ID Token and creates or logs in user
     */
    @Transactional
    public AuthResponse googleLogin(String idToken) {
        try {
            // Verify Google ID Token and get user info
            GoogleUserInfo googleUser = verifyGoogleToken(idToken);
            
            if (googleUser == null) {
                throw new BusinessException("無效的 Google Token");
            }

            // Check if user exists by email
            User user = userRepository.findByEmail(googleUser.getEmail())
                    .orElse(null);

            boolean isNewUser = (user == null);
            
            if (isNewUser) {
                // Create new user if doesn't exist
                user = User.builder()
                        .username(googleUser.getEmail().split("@")[0] + "_" + System.currentTimeMillis())
                        .email(googleUser.getEmail())
                        .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Random password for OAuth users
                        .role(Role.CUSTOMER) // Default role for Google OAuth users
                        .enabled(true)
                        .emailVerified(true) // Google 已驗證此 Email
                        .build();
                user = userRepository.save(user);
                
                // Create corresponding CRM member record
                createCrmMember(googleUser, user);
            } else {
                // 員工帳號只能用帳號密碼登入，避免以 Google 身分接管後台帳號
                if (user.getRole() != Role.CUSTOMER) {
                    throw new BusinessException("員工帳號請使用帳號密碼登入");
                }
                // Check if user account is enabled
                if (!user.getEnabled()) {
                    throw new BusinessException("此帳號已被停用，無法登入");
                }

                // 他人可能先以此 Email 註冊（未驗證）：Google 證明了 Email 擁有權，
                // 將帳號標記為已驗證並更換密碼，使先前設定的密碼失效
                if (!user.isEmailConfirmed()) {
                    user.setEmailVerified(true);
                    user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    user = userRepository.save(user);
                }
                
                // Update last login time for existing member
                updateMemberLastLogin(googleUser.getEmail());
            }

            // Generate JWT token
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .emailVerified(user.isEmailConfirmed())
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Google 登入失敗: " + e.getMessage());
        }
    }

    /**
     * 透過 Google 驗證 ID Token（簽章、有效期、aud、iss、email_verified）
     */
    private GoogleUserInfo verifyGoogleToken(String idToken) {
        if (!googleTokenVerifier.isEnabled()) {
            throw new BusinessException("Google 登入尚未啟用");
        }
        return googleTokenVerifier.verify(idToken)
                .map(user -> GoogleUserInfo.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .picture(user.getPicture())
                        .build())
                .orElse(null);
    }

    /**
     * Create CRM member record for new Google OAuth user
     */
    @Transactional
    private void createCrmMember(GoogleUserInfo googleUser, User user) {
        try {
            // Check if member already exists
            if (memberRepository.existsByEmail(googleUser.getEmail())) {
                return; // Member already exists, skip creation
            }

            // Create new member record
            Member member = Member.builder()
                    .name(googleUser.getName() != null ? googleUser.getName() : googleUser.getEmail().split("@")[0])
                    .email(googleUser.getEmail())
                    .status(MemberStatus.ACTIVE)
                    .totalPoints(0)
                    .availablePoints(0)
                    .registeredAt(java.time.LocalDateTime.now())
                    .lastLoginAt(java.time.LocalDateTime.now())
                    .build();

            memberRepository.save(member);
        } catch (Exception e) {
            // Log error but don't fail the login process
            System.err.println("Failed to create CRM member for Google user: " + e.getMessage());
        }
    }

    /**
     * Update member last login time
     */
    @Transactional
    private void updateMemberLastLogin(String email) {
        try {
            memberRepository.findByEmail(email).ifPresent(member -> {
                member.setLastLoginAt(java.time.LocalDateTime.now());
                memberRepository.save(member);
            });
        } catch (Exception e) {
            // Log error but don't fail the login process
            System.err.println("Failed to update member last login time: " + e.getMessage());
        }
    }

    /**
     * Inner class for Google user information
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class GoogleUserInfo {
        private String email;
        private String name;
        private String picture;
    }
}
