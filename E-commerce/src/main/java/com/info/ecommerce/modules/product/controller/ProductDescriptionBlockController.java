package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.ProductDescriptionBlockDTO;
import com.info.ecommerce.modules.product.service.ProductDescriptionBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/description-blocks")
@RequiredArgsConstructor
@Tag(name = "商品描述區塊管理", description = "商品描述區塊 CRUD 功能")
public class ProductDescriptionBlockController {

    private final ProductDescriptionBlockService blockService;
    private final com.info.ecommerce.modules.product.service.ProductService productService;
    private final com.info.ecommerce.modules.auth.service.CurrentUserService currentUserService;

    /** 未上架商品的描述內容只有後台看得到 */
    private void assertVisible(Long productId) {
        if (!currentUserService.isStaff()) {
            productService.assertPubliclyVisible(productId);
        }
    }

    @GetMapping
    @Operation(summary = "獲取商品的所有描述區塊")
    public ApiResponse<List<ProductDescriptionBlockDTO>> getProductBlocks(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        assertVisible(productId);
        return ApiResponse.success(blockService.getProductBlocks(productId));
    }

    @GetMapping("/manual")
    @Operation(summary = "獲取商品的手動區塊（區塊1~3）")
    public ApiResponse<List<ProductDescriptionBlockDTO>> getManualBlocks(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        assertVisible(productId);
        return ApiResponse.success(blockService.getManualBlocks(productId));
    }

    @GetMapping("/auto")
    @Operation(summary = "獲取商品的自動區塊（區塊1~7）")
    public ApiResponse<List<ProductDescriptionBlockDTO>> getAutoBlocks(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        assertVisible(productId);
        return ApiResponse.success(blockService.getAutoBlocks(productId));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/manual/{blockNumber}")
    @Operation(summary = "創建或更新手動區塊")
    public ApiResponse<ProductDescriptionBlockDTO> saveManualBlock(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "區塊編號（1-3）") @PathVariable Integer blockNumber,
            @Valid @RequestBody ProductDescriptionBlockDTO dto) {
        return ApiResponse.success("區塊已保存", blockService.saveManualBlock(productId, blockNumber, dto));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/auto/initialize")
    @Operation(summary = "初始化商品的自動區塊")
    public ApiResponse<List<ProductDescriptionBlockDTO>> initializeAutoBlocks(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        return ApiResponse.success("自動區塊已初始化", blockService.initializeAutoBlocks(productId));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/auto/{blockNumber}")
    @Operation(summary = "更新自動區塊")
    public ApiResponse<ProductDescriptionBlockDTO> updateAutoBlock(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "區塊編號（1-7）") @PathVariable Integer blockNumber,
            @Valid @RequestBody ProductDescriptionBlockDTO dto) {
        return ApiResponse.success("區塊已更新", blockService.updateAutoBlock(productId, blockNumber, dto));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/batch")
    @Operation(summary = "批量保存描述區塊")
    public ApiResponse<List<ProductDescriptionBlockDTO>> saveBlocks(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Valid @RequestBody List<ProductDescriptionBlockDTO> blocks) {
        return ApiResponse.success("區塊已保存", blockService.saveBlocks(productId, blocks));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{blockId}")
    @Operation(summary = "刪除描述區塊")
    public ApiResponse<Void> deleteBlock(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "區塊 ID") @PathVariable Long blockId) {
        blockService.deleteBlock(blockId);
        return ApiResponse.success("區塊已刪除", null);
    }
}

