package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.ProductImageDTO;
import com.info.ecommerce.modules.product.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Tag(name = "商品圖片", description = "商品圖片管理（每個商品支持 10 ~ 15 張圖片）")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    @Operation(summary = "添加商品圖片")
    public ApiResponse<ProductImageDTO> addProductImage(@Valid @RequestBody ProductImageDTO dto) {
        return ApiResponse.success("圖片已添加", productImageService.addProductImage(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品圖片")
    public ApiResponse<ProductImageDTO> updateProductImage(
            @Parameter(description = "圖片 ID") @PathVariable Long id,
            @Valid @RequestBody ProductImageDTO dto) {
        return ApiResponse.success("圖片已更新", productImageService.updateProductImage(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除商品圖片")
    public ApiResponse<Void> deleteProductImage(
            @Parameter(description = "圖片 ID") @PathVariable Long id) {
        productImageService.deleteProductImage(id);
        return ApiResponse.success("圖片已刪除", null);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "取得商品的所有圖片")
    public ApiResponse<List<ProductImageDTO>> listProductImages(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        return ApiResponse.success(productImageService.listProductImages(productId));
    }

    @PutMapping("/{id}/set-primary")
    @Operation(summary = "設置為主圖")
    public ApiResponse<ProductImageDTO> setPrimaryImage(
            @Parameter(description = "圖片 ID") @PathVariable Long id) {
        return ApiResponse.success("主圖已設置", productImageService.setPrimaryImage(id));
    }
}
