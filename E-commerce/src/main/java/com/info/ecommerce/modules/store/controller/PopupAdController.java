package com.info.ecommerce.modules.store.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.store.dto.PopupAdDTO;
import com.info.ecommerce.modules.store.service.PopupAdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popup-ads")
@RequiredArgsConstructor
@Tag(name = "彈跳廣告", description = "首頁彈跳廣告管理")
public class PopupAdController {

    private final PopupAdService popupAdService;

    @GetMapping
    @Operation(summary = "取得所有廣告（後台）")
    public ApiResponse<List<PopupAdDTO>> getAllAds() {
        return ApiResponse.success(popupAdService.getAllAds());
    }

    @GetMapping("/active")
    @Operation(summary = "取得有效廣告（前台）", description = "根據時間區間篩選")
    public ApiResponse<List<PopupAdDTO>> getActiveAds() {
        return ApiResponse.success(popupAdService.getActiveAds());
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得單一廣告")
    public ApiResponse<PopupAdDTO> getAd(@PathVariable Long id) {
        return ApiResponse.success(popupAdService.getAdById(id));
    }

    @PostMapping
    @Operation(summary = "新增廣告")
    public ApiResponse<PopupAdDTO> createAd(@Valid @RequestBody PopupAdDTO dto) {
        return ApiResponse.success("廣告已新增", popupAdService.createAd(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新廣告")
    public ApiResponse<PopupAdDTO> updateAd(@PathVariable Long id, @Valid @RequestBody PopupAdDTO dto) {
        return ApiResponse.success("廣告已更新", popupAdService.updateAd(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除廣告")
    public ApiResponse<Void> deleteAd(@PathVariable Long id) {
        popupAdService.deleteAd(id);
        return ApiResponse.success("廣告已刪除", null);
    }
}
