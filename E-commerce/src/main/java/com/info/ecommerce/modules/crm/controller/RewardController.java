package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.RewardConfigDTO;
import com.info.ecommerce.modules.crm.entity.RewardClaim;
import com.info.ecommerce.modules.crm.service.RewardService;
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

@RestController
@RequestMapping("/api/crm/rewards")
@RequiredArgsConstructor
@Tag(name = "獎勵制度管理", description = "CRM 獎勵制度管理功能（入會禮/生日禮）")
public class RewardController {

    private final RewardService rewardService;

    @PostMapping
    @Operation(summary = "創建獎勵設定")
    public ApiResponse<RewardConfigDTO> createRewardConfig(@Valid @RequestBody RewardConfigDTO dto) {
        return ApiResponse.success("獎勵設定已創建", rewardService.createRewardConfig(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新獎勵設定")
    public ApiResponse<RewardConfigDTO> updateRewardConfig(
            @Parameter(description = "獎勵設定 ID") @PathVariable Long id,
            @Valid @RequestBody RewardConfigDTO dto) {
        return ApiResponse.success("獎勵設定已更新", rewardService.updateRewardConfig(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得獎勵設定詳情")
    public ApiResponse<RewardConfigDTO> getRewardConfig(
            @Parameter(description = "獎勵設定 ID") @PathVariable Long id) {
        return ApiResponse.success(rewardService.getRewardConfig(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除獎勵設定")
    public ApiResponse<Void> deleteRewardConfig(
            @Parameter(description = "獎勵設定 ID") @PathVariable Long id) {
        rewardService.deleteRewardConfig(id);
        return ApiResponse.success("獎勵設定已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢獎勵設定")
    public ApiResponse<Page<RewardConfigDTO>> listRewardConfigs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(rewardService.listRewardConfigs(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的獎勵設定")
    public ApiResponse<List<RewardConfigDTO>> listEnabledRewardConfigs() {
        return ApiResponse.success(rewardService.listEnabledRewardConfigs());
    }

    @PostMapping("/claim/welcome/{memberId}")
    @Operation(summary = "領取入會禮")
    public ApiResponse<Void> claimWelcomeReward(
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        rewardService.claimWelcomeReward(memberId);
        return ApiResponse.success("入會禮已領取", null);
    }

    @PostMapping("/claim/birthday/{memberId}")
    @Operation(summary = "領取生日禮")
    public ApiResponse<Void> claimBirthdayReward(
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        rewardService.claimBirthdayReward(memberId);
        return ApiResponse.success("生日禮已領取", null);
    }

    @GetMapping("/claims/member/{memberId}")
    @Operation(summary = "取得會員的獎勵領取紀錄")
    public ApiResponse<Page<RewardClaim>> listMemberRewardClaims(
            @Parameter(description = "會員 ID") @PathVariable Long memberId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(rewardService.listMemberRewardClaims(memberId, pageable));
    }
}
