package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.entity.InventoryMovementLog;
import com.info.ecommerce.modules.product.service.InventoryManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "庫存管理", description = "庫存警示、貨到通知等功能")
public class InventoryManagementController {

    private final InventoryManagementService inventoryService;

    @PostMapping("/check-alerts")
    @Operation(summary = "檢查庫存並創建警示")
    public ApiResponse<Void> checkInventoryAndCreateAlerts() {
        inventoryService.checkInventoryAndCreateAlerts();
        return ApiResponse.success("庫存檢查完成", null);
    }

    @PutMapping("/alerts/{alertId}/resolve")
    @Operation(summary = "解決庫存警示")
    public ApiResponse<Void> resolveAlert(
            @Parameter(description = "警示 ID") @PathVariable Long alertId) {
        inventoryService.resolveAlert(alertId);
        return ApiResponse.success("警示已解決", null);
    }

    @GetMapping("/alerts")
    @Operation(summary = "取得未解決的警示")
    public ApiResponse<List<InventoryAlert>> getUnresolvedAlerts() {
        return ApiResponse.success(inventoryService.getUnresolvedAlerts());
    }

    @GetMapping("/alerts/product/{productId}")
    @Operation(summary = "取得指定商品的未解決警示")
    public ApiResponse<List<InventoryAlert>> getProductUnresolvedAlerts(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        return ApiResponse.success(inventoryService.getProductUnresolvedAlerts(productId));
    }

    @GetMapping("/logs")
    @Operation(summary = "取得庫存異動紀錄")
    public ApiResponse<List<InventoryMovementLog>> getInventoryMovementLogs() {
        return ApiResponse.success(inventoryService.getInventoryMovementLogs());
    }

    @GetMapping("/logs/product/{productId}")
    @Operation(summary = "取得商品庫存異動紀錄")
    public ApiResponse<List<InventoryMovementLog>> getProductInventoryMovementLogs(
            @Parameter(description = "?? ID") @PathVariable Long productId) {
        return ApiResponse.success(inventoryService.getProductInventoryMovementLogs(productId));
    }

    @PostMapping("/notifications/subscribe")
    @Operation(summary = "訂閱貨到通知")
    public ApiResponse<Void> subscribeStockNotification(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            @Parameter(description = "規格 ID") @RequestParam(required = false) Long specificationId,
            @Parameter(description = "Email") @RequestParam String email,
            @Parameter(description = "電話") @RequestParam(required = false) String phone) {
        inventoryService.subscribeStockNotification(productId, specificationId, email, phone);
        return ApiResponse.success("訂閱成功", null);
    }

    @PostMapping("/notifications/process/{productId}")
    @Operation(summary = "處理貨到通知")
    public ApiResponse<Void> processStockNotifications(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        inventoryService.processStockNotifications(productId);
        return ApiResponse.success("通知已發送", null);
    }

    @PutMapping("/update")
    @Operation(summary = "更新庫存")
    public ApiResponse<Void> updateInventory(
            @Parameter(description = "商品 ID") @RequestParam Long productId,
            @Parameter(description = "規格 ID") @RequestParam(required = false) Long specificationId,
            @Parameter(description = "倉庫 ID") @RequestParam Long warehouseId,
            @Parameter(description = "數量") @RequestParam Integer quantity) {
        inventoryService.updateInventory(productId, specificationId, warehouseId, quantity);
        return ApiResponse.success("庫存已更新", null);
    }
}
