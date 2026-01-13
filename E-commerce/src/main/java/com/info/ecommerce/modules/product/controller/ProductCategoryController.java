package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.ProductCategoryDTO;
import com.info.ecommerce.modules.product.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@Tag(name = "商品分類", description = "商品分類管理（支持 100 ~ 600 個分類）")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @PostMapping
    @Operation(summary = "創建分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductCategoryDTO> createCategory(@Valid @RequestBody ProductCategoryDTO dto) {
        return ApiResponse.success("分類已創建", categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductCategoryDTO> updateCategory(
            @Parameter(description = "分類 ID") @PathVariable Long id,
            @Valid @RequestBody ProductCategoryDTO dto) {
        return ApiResponse.success("分類已更新", categoryService.updateCategory(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得分類詳情")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<ProductCategoryDTO> getCategory(
            @Parameter(description = "分類 ID") @PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategory(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Void> deleteCategory(
            @Parameter(description = "分類 ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("分類已刪除", null);
    }

    @GetMapping
    @Operation(summary = "取得所有分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<List<ProductCategoryDTO>> listAllCategories() {
        return ApiResponse.success(categoryService.listAllCategories());
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<List<ProductCategoryDTO>> listEnabledCategories() {
        return ApiResponse.success(categoryService.listEnabledCategories());
    }

    @GetMapping("/top")
    @Operation(summary = "取得頂層分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<List<ProductCategoryDTO>> listTopCategories() {
        return ApiResponse.success(categoryService.listTopCategories());
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "取得子分類")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<List<ProductCategoryDTO>> listSubCategories(
            @Parameter(description = "父分類 ID") @PathVariable Long parentId) {
        return ApiResponse.success(categoryService.listSubCategories(parentId));
    }
}
