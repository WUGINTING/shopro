package com.info.ecommerce.modules.store.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.store.dto.StoreDTO;
import com.info.ecommerce.modules.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Tag(name = "商店設定", description = "商店基本設定管理")
public class StoreController {

    private final StoreService storeService;
    @PostMapping
    @Operation(summary = "初始化商店設定")
    public ApiResponse<StoreDTO> createStore(@Valid @RequestBody StoreDTO dto) {
        return ApiResponse.success("商店設定已初始化", storeService.createStore(dto));
    }
    @GetMapping
    @Operation(summary = "取得商店設定")
    public ApiResponse<StoreDTO> getStore() {
        return ApiResponse.success(storeService.getStore());
    }

    @PutMapping
    @Operation(summary = "更新商店設定")
    public ApiResponse<StoreDTO> updateStore(@Valid @RequestBody StoreDTO dto) {
        return ApiResponse.success("商店設定已更新", storeService.saveStore(dto));
    }
}
