package com.info.ecommerce.modules.marketing.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.marketing.dto.MarketingCampaignDTO;
import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import com.info.ecommerce.modules.marketing.service.MarketingCampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing/campaigns")
@RequiredArgsConstructor
@Tag(name = "營銷活動管理", description = "營銷活動管理功能")
public class MarketingCampaignController {

    private final MarketingCampaignService campaignService;

    @PostMapping
    @Operation(summary = "創建營銷活動")
    public ApiResponse<MarketingCampaignDTO> createCampaign(@Valid @RequestBody MarketingCampaignDTO dto) {
        return ApiResponse.success("營銷活動已創建", campaignService.createCampaign(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新營銷活動")
    public ApiResponse<MarketingCampaignDTO> updateCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @Valid @RequestBody MarketingCampaignDTO dto) {
        return ApiResponse.success("營銷活動已更新", campaignService.updateCampaign(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得營銷活動詳情")
    public ApiResponse<MarketingCampaignDTO> getCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success(campaignService.getCampaign(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除營銷活動")
    public ApiResponse<Void> deleteCampaign(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ApiResponse.success("營銷活動已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢營銷活動")
    public ApiResponse<Map<String, Object>> listCampaigns(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "狀態過濾") @RequestParam(required = false) CampaignStatus status,
            @Parameter(description = "類型過濾") @RequestParam(required = false) CampaignType type) {
        
        // 轉換為 0-based 頁碼
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Page<MarketingCampaignDTO> pageResult;
        if (status != null) {
            pageResult = campaignService.listCampaignsByStatus(status, pageable);
        } else if (type != null) {
            pageResult = campaignService.listCampaignsByType(type, pageable);
        } else {
            pageResult = campaignService.listCampaigns(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", pageResult.getContent());
        response.put("total", pageResult.getTotalElements());
        response.put("page", page);
        response.put("pageSize", pageSize);
        
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新活動狀態")
    public ApiResponse<MarketingCampaignDTO> updateCampaignStatus(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        CampaignStatus status = CampaignStatus.valueOf(request.get("status"));
        return ApiResponse.success("活動狀態已更新", campaignService.updateCampaignStatus(id, status));
    }
}
