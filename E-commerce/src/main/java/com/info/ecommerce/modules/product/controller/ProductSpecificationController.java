package com.info.ecommerce.modules.product.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.service.CurrentUserService;
import com.info.ecommerce.modules.product.dto.ProductSpecificationDTO;
import com.info.ecommerce.modules.product.service.ProductSpecificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-specifications")
@RequiredArgsConstructor
@Tag(name = "商品規格", description = "商品規格管理（支持多規格多價格）")
public class ProductSpecificationController {

    private final ProductSpecificationService specificationService;
    private final CurrentUserService currentUserService;
    private final com.info.ecommerce.modules.product.service.ProductService productService;

    /** 前台（非員工）不回傳成本價 */
    private ProductSpecificationDTO forCaller(ProductSpecificationDTO dto) {
        if (dto != null && !currentUserService.isStaff()) {
            dto.setCost(null);
        }
        return dto;
    }

    @PostMapping
    @Operation(summary = "添加商品規格")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ProductSpecificationDTO> addSpecification(@Valid @RequestBody ProductSpecificationDTO dto) {
        return ApiResponse.success("規格已添加", specificationService.addSpecification(dto));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量創建商品規格")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<List<ProductSpecificationDTO>> batchAddSpecifications(
            @Valid @RequestBody List<ProductSpecificationDTO> dtos) {
        return ApiResponse.success("規格已批量創建", specificationService.batchAddSpecifications(dtos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品規格")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<ProductSpecificationDTO> updateSpecification(
            @Parameter(description = "規格 ID") @PathVariable Long id,
            @Valid @RequestBody ProductSpecificationDTO dto) {
        return ApiResponse.success("規格已更新", specificationService.updateSpecification(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除商品規格")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Void> deleteSpecification(
            @Parameter(description = "規格 ID") @PathVariable Long id) {
        specificationService.deleteSpecification(id);
        return ApiResponse.success("規格已刪除", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得規格詳情")
    public ApiResponse<ProductSpecificationDTO> getSpecification(
            @Parameter(description = "規格 ID") @PathVariable Long id) {
        ProductSpecificationDTO spec = specificationService.getSpecification(id);
        if (!currentUserService.isStaff()) {
            productService.assertPubliclyVisible(spec.getProductId());
        }
        return ApiResponse.success(forCaller(spec));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "取得商品的所有規格")
    public ApiResponse<List<ProductSpecificationDTO>> listProductSpecifications(
            @Parameter(description = "商品 ID") @PathVariable Long productId) {
        if (!currentUserService.isStaff()) {
            productService.assertPubliclyVisible(productId);
        }
        List<ProductSpecificationDTO> specs = specificationService.listProductSpecifications(productId);
        if (!currentUserService.isStaff()) {
            // 顧客只看得到啟用中的規格
            specs = specs.stream().filter(spec -> !Boolean.FALSE.equals(spec.getEnabled())).toList();
        }
        specs.forEach(this::forCaller);
        return ApiResponse.success(specs);
    }
}
