package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.service.CurrentUserService;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.entity.MemberLevel;
import com.info.ecommerce.modules.crm.repository.MemberLevelRepository;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 會員中心：目前登入會員（Email 已驗證）的會員資料、等級、點數與預設收件資料
 */
@RestController
@RequestMapping("/api/account/member")
@RequiredArgsConstructor
@Tag(name = "會員中心", description = "登入會員自己的會員資料")
public class AccountMemberController {

    private final CurrentUserService currentUserService;
    private final MemberRepository memberRepository;
    private final MemberLevelRepository memberLevelRepository;

    @Data
    public static class UpdateRequest {
        @Size(max = 100, message = "姓名不可超過 100 字")
        private String name;
        @Pattern(regexp = "^$|^09\\d{8}$", message = "請輸入正確的手機號碼格式 (09xxxxxxxx)")
        private String phone;
        @Size(max = 500, message = "地址不可超過 500 字")
        private String address;
        @Size(max = 10, message = "郵遞區號不可超過 10 字")
        private String postalCode;
        private Boolean marketingOptIn;
    }

    @GetMapping
    @Operation(summary = "我的會員資料", description = "Email 尚未驗證或尚未下單過時 exists = false")
    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> get() {
        return ApiResponse.success(currentUserService.currentMember().map(this::toView).orElseGet(() -> {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("exists", false);
            empty.put("emailVerified", currentUserService.currentUser().map(User::isEmailConfirmed).orElse(false));
            return empty;
        }));
    }

    @PutMapping
    @Operation(summary = "更新我的預設收件資料與通知設定")
    @Transactional
    public ApiResponse<Map<String, Object>> update(@Valid @RequestBody UpdateRequest request) {
        User user = currentUserService.currentUser().orElseThrow(() -> new BusinessException("請先登入"));
        if (!user.isEmailConfirmed()) {
            throw new BusinessException("請先完成 Email 驗證");
        }
        Member member = currentUserService.currentMember().orElseGet(() -> Member.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .notes("會員中心建立")
                .build());
        if (request.getName() != null && !request.getName().isBlank()) {
            member.setName(request.getName().trim());
        }
        if (request.getPhone() != null) {
            member.setPhone(request.getPhone().isBlank() ? null : request.getPhone().trim());
        }
        if (request.getAddress() != null) {
            member.setAddress(request.getAddress().isBlank() ? null : request.getAddress().trim());
        }
        if (request.getPostalCode() != null) {
            member.setPostalCode(request.getPostalCode().isBlank() ? null : request.getPostalCode().trim());
        }
        if (request.getMarketingOptIn() != null) {
            member.setMarketingOptIn(request.getMarketingOptIn());
        }
        return ApiResponse.success("已儲存", toView(memberRepository.save(member)));
    }

    private Map<String, Object> toView(Member member) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("exists", true);
        view.put("emailVerified", true);
        view.put("name", member.getName());
        view.put("email", member.getEmail());
        view.put("phone", member.getPhone());
        view.put("address", member.getAddress());
        view.put("postalCode", member.getPostalCode());
        view.put("marketingOptIn", Boolean.TRUE.equals(member.getMarketingOptIn()));
        view.put("totalSpent", member.getTotalSpent() != null ? member.getTotalSpent() : BigDecimal.ZERO);
        view.put("availablePoints", member.getAvailablePoints() != null ? member.getAvailablePoints() : 0);
        MemberLevel level = member.getLevelId() == null ? null
                : memberLevelRepository.findById(member.getLevelId()).filter(l -> !Boolean.FALSE.equals(l.getEnabled())).orElse(null);
        view.put("levelName", level != null ? level.getName() : "一般會員");
        view.put("discountRate", level != null ? level.getDiscountRate() : null);
        // 下一個等級（依最低消費排序）
        BigDecimal spent = member.getTotalSpent() != null ? member.getTotalSpent() : BigDecimal.ZERO;
        memberLevelRepository.findAll().stream()
                .filter(l -> !Boolean.FALSE.equals(l.getEnabled()) && l.getMinSpendAmount() != null
                        && l.getMinSpendAmount().compareTo(spent) > 0)
                .min((a, b) -> a.getMinSpendAmount().compareTo(b.getMinSpendAmount()))
                .ifPresent(next -> {
                    view.put("nextLevelName", next.getName());
                    view.put("nextLevelRemaining", next.getMinSpendAmount().subtract(spent));
                });
        return view;
    }
}
