package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.CartReminderDTO;
import com.info.ecommerce.modules.crm.service.CartReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/crm/cart-reminders")
@RequiredArgsConstructor
@Tag(name = "購物車未結帳提醒", description = "CRM 購物車未結帳提醒功能")
public class CartReminderController {

    private final CartReminderService cartReminderService;

    @PostMapping
    @Operation(summary = "創建購物車提醒")
    public ApiResponse<CartReminderDTO> createCartReminder(@Valid @RequestBody CartReminderDTO dto) {
        return ApiResponse.success("購物車提醒已創建", cartReminderService.createCartReminder(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新購物車提醒")
    public ApiResponse<CartReminderDTO> updateCartReminder(
            @Parameter(description = "提醒 ID") @PathVariable Long id,
            @Valid @RequestBody CartReminderDTO dto) {
        return ApiResponse.success("購物車提醒已更新", cartReminderService.updateCartReminder(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得購物車提醒詳情")
    public ApiResponse<CartReminderDTO> getCartReminder(
            @Parameter(description = "提醒 ID") @PathVariable Long id) {
        return ApiResponse.success(cartReminderService.getCartReminder(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除購物車提醒")
    public ApiResponse<Void> deleteCartReminder(
            @Parameter(description = "提醒 ID") @PathVariable Long id) {
        cartReminderService.deleteCartReminder(id);
        return ApiResponse.success("購物車提醒已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢購物車提醒")
    public ApiResponse<Page<CartReminderDTO>> listCartReminders(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(cartReminderService.listCartReminders(pageable));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "取得會員的購物車提醒")
    public ApiResponse<Page<CartReminderDTO>> listCartRemindersByMember(
            @Parameter(description = "會員 ID") @PathVariable Long memberId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(cartReminderService.listCartRemindersByMember(memberId, pageable));
    }

    @GetMapping("/pending")
    @Operation(summary = "取得待發送的提醒")
    public ApiResponse<List<CartReminderDTO>> listPendingReminders(
            @Parameter(description = "截止時間（小時前）") @RequestParam(defaultValue = "24") int hours) {
        LocalDateTime beforeDate = LocalDateTime.now().minusHours(hours);
        return ApiResponse.success(cartReminderService.listPendingReminders(beforeDate));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "發送購物車提醒")
    public ApiResponse<CartReminderDTO> sendReminder(
            @Parameter(description = "提醒 ID") @PathVariable Long id) {
        return ApiResponse.success("購物車提醒已發送", cartReminderService.sendReminder(id));
    }

    @PostMapping("/send-pending")
    @Operation(summary = "批次發送待發送的提醒")
    public ApiResponse<Void> sendPendingReminders(
            @Parameter(description = "時間閾值（小時）") @RequestParam(defaultValue = "24") int hoursThreshold) {
        cartReminderService.sendPendingReminders(hoursThreshold);
        return ApiResponse.success("批次發送完成", null);
    }
}
