package com.info.ecommerce.modules.marketing.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.marketing.dto.PromotionDTO;
import com.info.ecommerce.modules.marketing.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing/promotions")
@RequiredArgsConstructor
@Tag(name = "促銷活動管理", description = "促銷活動管理功能")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @Operation(summary = "創建促銷活動")
    public ApiResponse<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO dto) {
        return ApiResponse.success("促銷活動已創建", promotionService.createPromotion(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新促銷活動")
    public ApiResponse<PromotionDTO> updatePromotion(
            @Parameter(description = "活動 ID") @PathVariable Long id,
            @Valid @RequestBody PromotionDTO dto) {
        return ApiResponse.success("促銷活動已更新", promotionService.updatePromotion(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得促銷活動詳情")
    public ApiResponse<PromotionDTO> getPromotion(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success(promotionService.getPromotion(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除促銷活動")
    public ApiResponse<Void> deletePromotion(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ApiResponse.success("促銷活動已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢促銷活動")
    public ApiResponse<Page<PromotionDTO>> listPromotions(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(promotionService.listPromotions(pageable));
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "啟用促銷活動")
    public ApiResponse<PromotionDTO> enablePromotion(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success("促銷活動已啟用", promotionService.enablePromotion(id));
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "停用促銷活動")
    public ApiResponse<PromotionDTO> disablePromotion(
            @Parameter(description = "活動 ID") @PathVariable Long id) {
        return ApiResponse.success("促銷活動已停用", promotionService.disablePromotion(id));
    }
}

