package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.MemberLevelDTO;
import com.info.ecommerce.modules.crm.service.MemberLevelService;
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
@RequestMapping("/api/crm/member-levels")
@RequiredArgsConstructor
@Tag(name = "會員等級管理", description = "CRM 會員等級管理功能")
public class MemberLevelController {

    private final MemberLevelService memberLevelService;

    @PostMapping
    @Operation(summary = "創建會員等級")
    public ApiResponse<MemberLevelDTO> createMemberLevel(@Valid @RequestBody MemberLevelDTO dto) {
        return ApiResponse.success("會員等級已創建", memberLevelService.createMemberLevel(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新會員等級")
    public ApiResponse<MemberLevelDTO> updateMemberLevel(
            @Parameter(description = "等級 ID") @PathVariable Long id,
            @Valid @RequestBody MemberLevelDTO dto) {
        return ApiResponse.success("會員等級已更新", memberLevelService.updateMemberLevel(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得會員等級詳情")
    public ApiResponse<MemberLevelDTO> getMemberLevel(
            @Parameter(description = "等級 ID") @PathVariable Long id) {
        return ApiResponse.success(memberLevelService.getMemberLevel(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除會員等級")
    public ApiResponse<Void> deleteMemberLevel(
            @Parameter(description = "等級 ID") @PathVariable Long id) {
        memberLevelService.deleteMemberLevel(id);
        return ApiResponse.success("會員等級已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢會員等級")
    public ApiResponse<Page<MemberLevelDTO>> listMemberLevels(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberLevelService.listMemberLevels(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "取得所有會員等級")
    public ApiResponse<List<MemberLevelDTO>> listAllMemberLevels() {
        return ApiResponse.success(memberLevelService.listAllMemberLevels());
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的會員等級")
    public ApiResponse<List<MemberLevelDTO>> listEnabledMemberLevels() {
        return ApiResponse.success(memberLevelService.listEnabledMemberLevels());
    }

    @PutMapping("/{id}/toggle-enabled")
    @Operation(summary = "切換會員等級啟用狀態")
    public ApiResponse<MemberLevelDTO> toggleEnabled(
            @Parameter(description = "等級 ID") @PathVariable Long id) {
        return ApiResponse.success("會員等級狀態已切換", memberLevelService.toggleEnabled(id));
    }
}
