package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * 目前登入者的身分與資料擁有權檢查。
 * 會員（CUSTOMER）與 CRM 會員資料以 Email 對應。
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public Optional<User> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof User user) {
            return Optional.of(user);
        }
        return userRepository.findByUsername(authentication.getName());
    }

    /** 是否為後台員工（ADMIN / MANAGER / STAFF） */
    public boolean isStaff() {
        return currentUser().map(user -> user.getRole() != null && user.getRole() != Role.CUSTOMER).orElse(false);
    }

    /**
     * 目前會員對應的 CRM 會員（以 Email 對應）。
     * 會員帳號的 Email 必須已驗證，否則任何人都能註冊他人 Email 來查看對方的訂單。
     */
    public Optional<Member> currentMember() {
        return currentUser()
                .filter(this::emailTrusted)
                .map(User::getEmail)
                .flatMap(memberRepository::findByEmail);
    }

    private boolean emailTrusted(User user) {
        return user.getRole() != Role.CUSTOMER || user.isEmailConfirmed();
    }

    /**
     * 員工可存取任何訂單；會員只能存取自己的訂單（依會員 ID 或下單 Email 比對）
     */
    public void assertCanAccessOrder(Long orderCustomerId, String orderCustomerEmail) {
        if (isStaff()) {
            return;
        }
        User user = currentUser().orElseThrow(() -> new AccessDeniedException("請先登入"));
        boolean ownsByEmail = emailTrusted(user) && orderCustomerEmail != null && user.getEmail() != null
                && orderCustomerEmail.trim().equalsIgnoreCase(user.getEmail().trim());
        boolean ownsByMember = orderCustomerId != null && currentMember()
                .map(member -> Objects.equals(member.getId(), orderCustomerId))
                .orElse(false);
        if (!ownsByEmail && !ownsByMember) {
            throw new AccessDeniedException("無權存取此訂單");
        }
    }

    /** 會員只能查詢自己的會員 ID */
    public void assertCanAccessCustomer(Long customerId) {
        if (isStaff()) {
            return;
        }
        Long memberId = currentMember().map(Member::getId)
                .orElseThrow(() -> new AccessDeniedException("無權存取此會員資料"));
        if (!memberId.equals(customerId)) {
            throw new AccessDeniedException("無權存取此會員資料");
        }
    }
}
