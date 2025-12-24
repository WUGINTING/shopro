package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.MemberDTO;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import com.info.ecommerce.modules.crm.service.MemberService;
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
@RequestMapping("/api/crm/members")
@RequiredArgsConstructor
@Tag(name = "會員管理", description = "CRM 會員管理功能")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "創建會員")
    public ApiResponse<MemberDTO> createMember(@Valid @RequestBody MemberDTO dto) {
        return ApiResponse.success("會員已創建", memberService.createMember(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新會員")
    public ApiResponse<MemberDTO> updateMember(
            @Parameter(description = "會員 ID") @PathVariable Long id,
            @Valid @RequestBody MemberDTO dto) {
        return ApiResponse.success("會員已更新", memberService.updateMember(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得會員詳情")
    public ApiResponse<MemberDTO> getMember(
            @Parameter(description = "會員 ID") @PathVariable Long id) {
        return ApiResponse.success(memberService.getMember(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "依電子郵件取得會員")
    public ApiResponse<MemberDTO> getMemberByEmail(
            @Parameter(description = "電子郵件") @PathVariable String email) {
        return ApiResponse.success(memberService.getMemberByEmail(email));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除會員")
    public ApiResponse<Void> deleteMember(
            @Parameter(description = "會員 ID") @PathVariable Long id) {
        memberService.deleteMember(id);
        return ApiResponse.success("會員已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢會員")
    public ApiResponse<Page<MemberDTO>> listMembers(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberService.listMembers(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "依狀態查詢會員")
    public ApiResponse<Page<MemberDTO>> listMembersByStatus(
            @Parameter(description = "會員狀態") @PathVariable MemberStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberService.listMembersByStatus(status, pageable));
    }

    @GetMapping("/level/{levelId}")
    @Operation(summary = "依等級查詢會員")
    public ApiResponse<Page<MemberDTO>> listMembersByLevel(
            @Parameter(description = "等級 ID") @PathVariable Long levelId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberService.listMembersByLevel(levelId, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋會員")
    public ApiResponse<Page<MemberDTO>> searchMembers(
            @Parameter(description = "關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(memberService.searchMembers(keyword, pageable));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新會員狀態")
    public ApiResponse<MemberDTO> updateMemberStatus(
            @Parameter(description = "會員 ID") @PathVariable Long id,
            @Parameter(description = "會員狀態") @RequestParam MemberStatus status) {
        return ApiResponse.success("會員狀態已更新", memberService.updateMemberStatus(id, status));
    }

    @PutMapping("/{id}/level")
    @Operation(summary = "更新會員等級")
    public ApiResponse<MemberDTO> updateMemberLevel(
            @Parameter(description = "會員 ID") @PathVariable Long id,
            @Parameter(description = "等級 ID") @RequestParam Long levelId) {
        return ApiResponse.success("會員等級已更新", memberService.updateMemberLevel(id, levelId));
    }

    @PostMapping("/{id}/points/add")
    @Operation(summary = "增加會員積點")
    public ApiResponse<MemberDTO> addPoints(
            @Parameter(description = "會員 ID") @PathVariable Long id,
            @Parameter(description = "積點數量") @RequestParam Integer points) {
        return ApiResponse.success("積點已增加", memberService.addPoints(id, points));
    }

    @PostMapping("/{id}/points/deduct")
    @Operation(summary = "扣除會員積點")
    public ApiResponse<MemberDTO> deductPoints(
            @Parameter(description = "會員 ID") @PathVariable Long id,
            @Parameter(description = "積點數量") @RequestParam Integer points) {
        return ApiResponse.success("積點已扣除", memberService.deductPoints(id, points));
    }
}
