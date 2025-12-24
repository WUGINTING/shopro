package com.info.ecommerce.modules.crm.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.crm.dto.CustomPageDTO;
import com.info.ecommerce.modules.crm.service.CustomPageService;
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
@RequestMapping("/api/crm/custom-pages")
@RequiredArgsConstructor
@Tag(name = "自訂頁面管理", description = "CRM 自訂頁面管理功能")
public class CustomPageController {

    private final CustomPageService customPageService;

    @PostMapping
    @Operation(summary = "創建自訂頁面")
    public ApiResponse<CustomPageDTO> createCustomPage(@Valid @RequestBody CustomPageDTO dto) {
        return ApiResponse.success("自訂頁面已創建", customPageService.createCustomPage(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新自訂頁面")
    public ApiResponse<CustomPageDTO> updateCustomPage(
            @Parameter(description = "頁面 ID") @PathVariable Long id,
            @Valid @RequestBody CustomPageDTO dto) {
        return ApiResponse.success("自訂頁面已更新", customPageService.updateCustomPage(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得自訂頁面詳情")
    public ApiResponse<CustomPageDTO> getCustomPage(
            @Parameter(description = "頁面 ID") @PathVariable Long id) {
        return ApiResponse.success(customPageService.getCustomPage(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "依別名取得自訂頁面")
    public ApiResponse<CustomPageDTO> getCustomPageBySlug(
            @Parameter(description = "頁面別名") @PathVariable String slug) {
        return ApiResponse.success(customPageService.getCustomPageBySlug(slug));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除自訂頁面")
    public ApiResponse<Void> deleteCustomPage(
            @Parameter(description = "頁面 ID") @PathVariable Long id) {
        customPageService.deleteCustomPage(id);
        return ApiResponse.success("自訂頁面已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢自訂頁面")
    public ApiResponse<Page<CustomPageDTO>> listCustomPages(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(customPageService.listCustomPages(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "取得所有自訂頁面")
    public ApiResponse<List<CustomPageDTO>> listAllCustomPages() {
        return ApiResponse.success(customPageService.listAllCustomPages());
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的自訂頁面")
    public ApiResponse<List<CustomPageDTO>> listEnabledCustomPages() {
        return ApiResponse.success(customPageService.listEnabledCustomPages());
    }

    @PutMapping("/{id}/toggle-enabled")
    @Operation(summary = "切換自訂頁面啟用狀態")
    public ApiResponse<CustomPageDTO> toggleEnabled(
            @Parameter(description = "頁面 ID") @PathVariable Long id) {
        return ApiResponse.success("自訂頁面狀態已切換", customPageService.toggleEnabled(id));
    }
}
