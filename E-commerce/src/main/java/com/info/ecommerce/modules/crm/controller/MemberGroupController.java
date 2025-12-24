package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.MemberGroupDTO;
import com.info.ecommerce.modules.crm.service.MemberGroupService;
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
@RequestMapping("/api/crm/member-groups")
@RequiredArgsConstructor
@Tag(name = "會員群組管理", description = "CRM 會員群組管理功能")
public class MemberGroupController {

    private final MemberGroupService memberGroupService;

    @PostMapping
    @Operation(summary = "創建會員群組")
    public ApiResponse<MemberGroupDTO> createMemberGroup(@Valid @RequestBody MemberGroupDTO dto) {
        return ApiResponse.success("會員群組已創建", memberGroupService.createMemberGroup(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新會員群組")
    public ApiResponse<MemberGroupDTO> updateMemberGroup(
            @Parameter(description = "群組 ID") @PathVariable Long id,
            @Valid @RequestBody MemberGroupDTO dto) {
        return ApiResponse.success("會員群組已更新", memberGroupService.updateMemberGroup(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得會員群組詳情")
    public ApiResponse<MemberGroupDTO> getMemberGroup(
            @Parameter(description = "群組 ID") @PathVariable Long id) {
        return ApiResponse.success(memberGroupService.getMemberGroup(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除會員群組")
    public ApiResponse<Void> deleteMemberGroup(
            @Parameter(description = "群組 ID") @PathVariable Long id) {
        memberGroupService.deleteMemberGroup(id);
        return ApiResponse.success("會員群組已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢會員群組")
    public ApiResponse<Page<MemberGroupDTO>> listMemberGroups(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberGroupService.listMemberGroups(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的會員群組")
    public ApiResponse<List<MemberGroupDTO>> listEnabledMemberGroups() {
        return ApiResponse.success(memberGroupService.listEnabledMemberGroups());
    }

    @PostMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "將會員加入群組")
    public ApiResponse<Void> addMemberToGroup(
            @Parameter(description = "群組 ID") @PathVariable Long groupId,
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        memberGroupService.addMemberToGroup(memberId, groupId);
        return ApiResponse.success("會員已加入群組", null);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "將會員從群組移除")
    public ApiResponse<Void> removeMemberFromGroup(
            @Parameter(description = "群組 ID") @PathVariable Long groupId,
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        memberGroupService.removeMemberFromGroup(memberId, groupId);
        return ApiResponse.success("會員已從群組移除", null);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "取得會員所屬群組")
    public ApiResponse<List<Long>> getMemberGroups(
            @Parameter(description = "會員 ID") @PathVariable Long memberId) {
        return ApiResponse.success(memberGroupService.getMemberGroups(memberId));
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "取得群組內的會員")
    public ApiResponse<List<Long>> getGroupMembers(
            @Parameter(description = "群組 ID") @PathVariable Long groupId) {
        return ApiResponse.success(memberGroupService.getGroupMembers(groupId));
    }
}
