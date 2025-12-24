package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.entity.ProductTag;
import com.info.ecommerce.modules.product.service.ProductTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-tags")
@RequiredArgsConstructor
@Tag(name = "商品標籤", description = "商品標籤管理（支持 100 個 ~ 不限）")
public class ProductTagController {

    private final ProductTagService productTagService;

    @PostMapping
    @Operation(summary = "創建標籤")
    public ApiResponse<ProductTag> createTag(@Valid @RequestBody ProductTag tag) {
        return ApiResponse.success("標籤已創建", productTagService.createTag(tag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新標籤")
    public ApiResponse<ProductTag> updateTag(
            @Parameter(description = "標籤 ID") @PathVariable Long id,
            @Valid @RequestBody ProductTag tag) {
        return ApiResponse.success("標籤已更新", productTagService.updateTag(id, tag));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除標籤")
    public ApiResponse<Void> deleteTag(
            @Parameter(description = "標籤 ID") @PathVariable Long id) {
        productTagService.deleteTag(id);
        return ApiResponse.success("標籤已刪除", null);
    }

    @GetMapping
    @Operation(summary = "取得所有標籤")
    public ApiResponse<List<ProductTag>> listAllTags() {
        return ApiResponse.success(productTagService.listAllTags());
    }

    @PostMapping("/product/{productId}/tag/{tagId}")
    @Operation(summary = "為商品添加標籤")
    public ApiResponse<Void> addTagToProduct(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "標籤 ID") @PathVariable Long tagId) {
        productTagService.addTagToProduct(productId, tagId);
        return ApiResponse.success("標籤已添加", null);
    }

    @DeleteMapping("/product/{productId}/tag/{tagId}")
    @Operation(summary = "移除商品標籤")
    public ApiResponse<Void> removeTagFromProduct(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @Parameter(description = "標籤 ID") @PathVariable Long tagId) {
        productTagService.removeTagFromProduct(productId, tagId);
        return ApiResponse.success("標籤已移除", null);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "取得商品的所有標籤")
    public ApiResponse<List<ProductTag>> listProductTags(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        return ApiResponse.success(productTagService.listProductTags(productId));
    }

    @PutMapping("/product/{productId}")
    @Operation(summary = "批量設置商品標籤（替換現有標籤）")
    public ApiResponse<Void> setProductTags(
            @Parameter(description = "商品 ID") @PathVariable Long productId,
            @RequestBody List<Long> tagIds) {
        productTagService.setProductTags(productId, tagIds);
        return ApiResponse.success("標籤已設置", null);
    }
}
