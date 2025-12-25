package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.StoreDTO;
import com.info.ecommerce.modules.system.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 門市管理控制器
 */
@RestController
@RequestMapping("/api/system/stores")
@RequiredArgsConstructor
@Tag(name = "門市管理", description = "門市 CRUD 及管理功能")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @Operation(summary = "創建門市")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<StoreDTO> createStore(@Valid @RequestBody StoreDTO dto) {
        return ApiResponse.success("門市已創建", storeService.createStore(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新門市")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<StoreDTO> updateStore(
            @Parameter(description = "門市 ID") @PathVariable Long id,
            @Valid @RequestBody StoreDTO dto) {
        return ApiResponse.success("門市已更新", storeService.updateStore(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得門市詳情")
    public ApiResponse<StoreDTO> getStore(
            @Parameter(description = "門市 ID") @PathVariable Long id) {
        return ApiResponse.success(storeService.getStore(id));
    }

    @GetMapping("/code/{storeCode}")
    @Operation(summary = "依門市代碼取得門市")
    public ApiResponse<StoreDTO> getStoreByCode(
            @Parameter(description = "門市代碼") @PathVariable String storeCode) {
        return ApiResponse.success(storeService.getStoreByCode(storeCode));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除門市")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteStore(
            @Parameter(description = "門市 ID") @PathVariable Long id) {
        storeService.deleteStore(id);
        return ApiResponse.success("門市已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢門市")
    public ApiResponse<Page<StoreDTO>> listStores(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(storeService.listStores(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的門市")
    public ApiResponse<List<StoreDTO>> listEnabledStores() {
        return ApiResponse.success(storeService.listEnabledStores());
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "依城市查詢門市")
    public ApiResponse<Page<StoreDTO>> listByCity(
            @Parameter(description = "城市") @PathVariable String city,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(storeService.listByCity(city, pageable));
    }

    @GetMapping("/pickup")
    @Operation(summary = "取得支援取貨的門市")
    public ApiResponse<List<StoreDTO>> listPickupStores() {
        return ApiResponse.success(storeService.listPickupStores());
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋門市")
    public ApiResponse<Page<StoreDTO>> searchStores(
            @Parameter(description = "關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(storeService.searchStores(keyword, pageable));
    }
}
