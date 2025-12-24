package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.WarehouseDTO;
import com.info.ecommerce.modules.product.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "倉庫管理", description = "倉儲管理功能")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(summary = "創建倉庫")
    public ApiResponse<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseDTO dto) {
        return ApiResponse.success("倉庫已創建", warehouseService.createWarehouse(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新倉庫")
    public ApiResponse<WarehouseDTO> updateWarehouse(
            @Parameter(description = "倉庫 ID") @PathVariable Long id,
            @Valid @RequestBody WarehouseDTO dto) {
        return ApiResponse.success("倉庫已更新", warehouseService.updateWarehouse(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得倉庫詳情")
    public ApiResponse<WarehouseDTO> getWarehouse(
            @Parameter(description = "倉庫 ID") @PathVariable Long id) {
        return ApiResponse.success(warehouseService.getWarehouse(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除倉庫")
    public ApiResponse<Void> deleteWarehouse(
            @Parameter(description = "倉庫 ID") @PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.success("倉庫已刪除", null);
    }

    @GetMapping
    @Operation(summary = "取得所有倉庫")
    public ApiResponse<List<WarehouseDTO>> listAllWarehouses() {
        return ApiResponse.success(warehouseService.listAllWarehouses());
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的倉庫")
    public ApiResponse<List<WarehouseDTO>> listEnabledWarehouses() {
        return ApiResponse.success(warehouseService.listEnabledWarehouses());
    }

    @GetMapping("/default")
    @Operation(summary = "取得預設倉庫")
    public ApiResponse<WarehouseDTO> getDefaultWarehouse() {
        return ApiResponse.success(warehouseService.getDefaultWarehouse());
    }
}
