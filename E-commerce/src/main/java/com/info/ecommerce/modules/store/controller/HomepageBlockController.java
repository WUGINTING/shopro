package com.info.ecommerce.modules.store.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.store.dto.HomepageBlockDTO;
import com.info.ecommerce.modules.store.service.HomepageBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homepage-blocks")
@RequiredArgsConstructor
@Tag(name = "首頁區塊", description = "首頁設計區塊管理（Silver: 4 個 / Gold: 7 個）")
public class HomepageBlockController {

    private final HomepageBlockService homepageBlockService;

    @GetMapping
    @Operation(summary = "取得所有區塊（後台）")
    public ApiResponse<List<HomepageBlockDTO>> getAllBlocks() {
        return ApiResponse.success(homepageBlockService.getAllBlocks());
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得啟用的區塊（前台）")
    public ApiResponse<List<HomepageBlockDTO>> getEnabledBlocks() {
        return ApiResponse.success(homepageBlockService.getEnabledBlocks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得單一區塊")
    public ApiResponse<HomepageBlockDTO> getBlock(@PathVariable Long id) {
        return ApiResponse.success(homepageBlockService.getBlockById(id));
    }

    @PostMapping
    @Operation(summary = "新增區塊")
    public ApiResponse<HomepageBlockDTO> createBlock(@Valid @RequestBody HomepageBlockDTO dto) {
        return ApiResponse.success("區塊已新增", homepageBlockService.createBlock(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新區塊")
    public ApiResponse<HomepageBlockDTO> updateBlock(
            @PathVariable Long id,
            @Valid @RequestBody HomepageBlockDTO dto) {
        return ApiResponse.success("區塊已更新", homepageBlockService.updateBlock(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除區塊")
    public ApiResponse<Void> deleteBlock(@PathVariable Long id) {
        homepageBlockService.deleteBlock(id);
        return ApiResponse.success("區塊已刪除", null);
    }
}
