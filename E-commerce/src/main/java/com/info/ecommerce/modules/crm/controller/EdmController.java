package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.EdmCampaignDTO;
import com.info.ecommerce.modules.crm.entity.EdmSendLog;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import com.info.ecommerce.modules.crm.service.EdmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/crm/edm")
@RequiredArgsConstructor
@Tag(name = "EDM 電子報管理", description = "CRM EDM 電子報管理功能")
public class EdmController {

    private final EdmService edmService;

    @PostMapping
    @Operation(summary = "創建 EDM 活動")
    public ApiResponse<EdmCampaignDTO> createEdmCampaign(@Valid @RequestBody EdmCampaignDTO dto) {
        return ApiResponse.success("EDM 活動已創建", edmService.createEdmCampaign(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 EDM 活動")
    public ApiResponse<EdmCampaignDTO> updateEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @Valid @RequestBody EdmCampaignDTO dto) {
        return ApiResponse.success("EDM 活動已更新", edmService.updateEdmCampaign(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得 EDM 活動詳情")
    public ApiResponse<EdmCampaignDTO> getEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success(edmService.getEdmCampaign(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除 EDM 活動")
    public ApiResponse<Void> deleteEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        edmService.deleteEdmCampaign(id);
        return ApiResponse.success("EDM 活動已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢 EDM 活動")
    public ApiResponse<Page<EdmCampaignDTO>> listEdmCampaigns(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(edmService.listEdmCampaigns(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "依狀態查詢 EDM 活動")
    public ApiResponse<Page<EdmCampaignDTO>> listEdmCampaignsByStatus(
            @Parameter(description = "EDM 狀態") @PathVariable EdmStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(edmService.listEdmCampaignsByStatus(status, pageable));
    }

    @PostMapping("/{id}/schedule")
    @Operation(summary = "排程 EDM 活動")
    public ApiResponse<EdmCampaignDTO> scheduleEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @Parameter(description = "排程時間") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        return ApiResponse.success("EDM 活動已排程", edmService.scheduleEdmCampaign(id, scheduledAt));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "發送 EDM 活動")
    public ApiResponse<EdmCampaignDTO> sendEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success("EDM 活動已發送", edmService.sendEdmCampaign(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消 EDM 活動")
    public ApiResponse<EdmCampaignDTO> cancelEdmCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success("EDM 活動已取消", edmService.cancelEdmCampaign(id));
    }

    @GetMapping("/{id}/logs")
    @Operation(summary = "取得 EDM 發送紀錄")
    public ApiResponse<Page<EdmSendLog>> getEdmSendLogs(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(edmService.getEdmSendLogs(id, pageable));
    }
}
