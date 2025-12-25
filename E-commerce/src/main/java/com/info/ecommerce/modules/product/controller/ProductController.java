package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.product.dto.ProductDTO;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.service.ProductService;
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

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品 CRUD 及管理功能")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "創建商品")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> createProduct(@Valid @RequestBody ProductDTO dto) {
        return ApiResponse.success("商品已創建", productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> updateProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        return ApiResponse.success("商品已更新", productService.updateProduct(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得商品詳情")
    public ApiResponse<ProductDTO> getProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除商品")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("商品已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢商品")
    public ApiResponse<Page<ProductDTO>> listProducts(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(productService.listProducts(pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "依分類查詢商品")
    public ApiResponse<Page<ProductDTO>> listProductsByCategory(
            @Parameter(description = "分類 ID") @PathVariable Long categoryId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(productService.listProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "依狀態查詢商品")
    public ApiResponse<Page<ProductDTO>> listProductsByStatus(
            @Parameter(description = "商品狀態") @PathVariable ProductStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(productService.listProductsByStatus(status, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋商品")
    public ApiResponse<Page<ProductDTO>> searchProducts(
            @Parameter(description = "關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(productService.searchProducts(keyword, pageable));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "上架商品")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> activateProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        return ApiResponse.success("商品已上架", productService.activateProduct(id));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "下架商品")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> deactivateProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        return ApiResponse.success("商品已下架", productService.deactivateProduct(id));
    }

    @PostMapping("/{id}/album-images")
    @Operation(summary = "從相冊添加圖片到商品")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductDTO> addAlbumImagesToProduct(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Parameter(description = "相冊圖片 ID 列表") @RequestBody java.util.List<Long> albumImageIds) {
        return ApiResponse.success("圖片已添加", productService.addAlbumImagesToProduct(id, albumImageIds));
    }
}
