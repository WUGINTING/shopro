package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.CustomerBlacklistDTO;
import com.info.ecommerce.modules.order.service.CustomerBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 顧客黑名單控制器 - 黑名單管理
 */
@RestController
@RequestMapping("/api/orders/blacklist")
@RequiredArgsConstructor
@Tag(name = "顧客黑名單", description = "封鎖特定顧客並拒絕其交易")
public class CustomerBlacklistController {

    private final CustomerBlacklistService customerBlacklistService;

    @PostMapping
    @Operation(summary = "新增顧客到黑名單", description = "將顧客加入黑名單，拒絕其交易")
    public ApiResponse<CustomerBlacklistDTO> addToBlacklist(@Valid @RequestBody CustomerBlacklistDTO dto) {
        return ApiResponse.success("已加入黑名單", customerBlacklistService.addToBlacklist(dto));
    }

    @PatchMapping("/{blacklistId}/remove")
    @Operation(summary = "從黑名單移除", description = "將顧客從黑名單移除")
    public ApiResponse<CustomerBlacklistDTO> removeFromBlacklist(
            @Parameter(description = "黑名單記錄 ID") @PathVariable Long blacklistId) {
        return ApiResponse.success("已從黑名單移除", 
            customerBlacklistService.removeFromBlacklist(blacklistId));
    }

    @GetMapping("/check/{customerId}")
    @Operation(summary = "檢查顧客是否在黑名單中")
    public ApiResponse<Boolean> isBlacklisted(
            @Parameter(description = "客戶 ID") @PathVariable Long customerId) {
        return ApiResponse.success(customerBlacklistService.isBlacklisted(customerId));
    }

    @GetMapping("/active")
    @Operation(summary = "取得所有啟用的黑名單")
    public ApiResponse<List<CustomerBlacklistDTO>> getActiveBlacklist() {
        return ApiResponse.success(customerBlacklistService.getActiveBlacklist());
    }

    @GetMapping
    @Operation(summary = "分頁取得黑名單")
    public ApiResponse<Page<CustomerBlacklistDTO>> getBlacklistPage(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(customerBlacklistService.getBlacklistPage(pageable));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "取得顧客的黑名單記錄")
    public ApiResponse<CustomerBlacklistDTO> getByCustomerId(
            @Parameter(description = "客戶 ID") @PathVariable Long customerId) {
        return ApiResponse.success(customerBlacklistService.getByCustomerId(customerId));
    }

    @PatchMapping("/{blacklistId}/reason")
    @Operation(summary = "更新黑名單原因")
    public ApiResponse<CustomerBlacklistDTO> updateReason(
            @Parameter(description = "黑名單記錄 ID") @PathVariable Long blacklistId,
            @Parameter(description = "黑名單原因") @RequestParam String reason) {
        return ApiResponse.success("黑名單原因已更新", 
            customerBlacklistService.updateReason(blacklistId, reason));
    }
}
